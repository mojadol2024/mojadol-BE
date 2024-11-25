package com.gnu.mojadol.service.impl;

import com.gnu.mojadol.dto.PaymentDto;
import com.gnu.mojadol.entity.Payment;
import com.gnu.mojadol.repository.PaymentRepository;
import com.gnu.mojadol.service.PaymentService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    public void add(PaymentDto paymentDto) {

        Payment payment = new Payment();
        payment.setReceiptId(paymentDto.getReceiptId());
        payment.setUser(paymentDto.getUser());
        payment.setPg(paymentDto.getPg());
        payment.setMethod(paymentDto.getMethod());
        payment.setPrice(paymentDto.getPrice());
        payment.setPurchasedAt(paymentDto.getPurchasedAt());
        payment.setReceiptUrl(paymentDto.getReceiptUrl());
        payment.setCardNum(paymentDto.getCardNum());
        payment.setCardCompany(paymentDto.getCardCompany());
        payment.setDel(paymentDto.getDel());

        paymentRepository.save(payment);
    }

    public void cancel(String receiptId) {
        Payment payment = paymentRepository.findByReceiptId(receiptId);

        payment.setDel("결제 취소");

        paymentRepository.save(payment);
    }
}
