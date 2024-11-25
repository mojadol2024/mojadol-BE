package com.gnu.mojadol.controller;

import com.gnu.mojadol.dto.PaymentDto;
import com.gnu.mojadol.entity.User;
import com.gnu.mojadol.repository.UserRepository;
import com.gnu.mojadol.service.PaymentService;
import com.gnu.mojadol.utils.JwtUtil;
import com.google.api.Http;
import kr.co.bootpay.Bootpay;
import kr.co.bootpay.model.request.Cancel;
import kr.co.bootpay.model.response.ResDefault;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Value("${bootpay_rest_api_key}")
    private String rest_key;

    @Value("${bootpay_private_key}")
    private String private_key;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/add")
    public ResponseEntity<?> addPay (@RequestBody PaymentDto paymentDto, @RequestHeader("Authorization") String accessToken) {
        System.out.println("PaymentController pay" + new Date());
        String receiptId = paymentDto.getReceiptId();
        try {
            // receiptId가 유효한지 검증 후 db에 저장 해야함
            Bootpay bootpay = new Bootpay(rest_key, private_key);

            HashMap<String, Object> res = bootpay.getReceipt(receiptId);
            if(res.get("error_code") == null) {
                String userId = jwtUtil.extractUsername(accessToken);
                User user = userRepository.findByUserId(userId);

                paymentDto.setUser(user);

                paymentService.add(paymentDto);

                return ResponseEntity.ok("YES");
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("유효하지 않은 결제입니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("잘못된 요청입니다.");
        }
    }

    @PostMapping("/cancel")
    public ResponseEntity<?> cancel(@RequestParam String receiptId, @RequestHeader("Authorization") String accessToken) {
        System.out.println("PaymentController cancel " + new Date());
        try {
            // REST API KEY, PRIVATE KEY
            Bootpay bootpay = new Bootpay(rest_key, private_key);

            Cancel cancel = new Cancel();
            cancel.receiptId = receiptId;
            cancel.cancelUsername = "관리자";
            cancel.cancelMessage = "테스트 결제 취소";

            HashMap<String, Object> res = bootpay.receiptCancel(cancel);

            String userId = jwtUtil.extractUsername(accessToken);
            User user = userRepository.findByUserId(userId);
            if(res.get("error_code") == null && user != null) { //success
                System.out.println("receiptCancel success: " + res);
                // 결제취소
                paymentService.cancel(receiptId);
            }
            return ResponseEntity.ok("YES");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("잘못된 요청입니다.");
        }

    }

}
