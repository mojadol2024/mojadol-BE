package com.gnu.mojadol.controller;

import com.gnu.mojadol.dto.*;
import com.gnu.mojadol.entity.User;
import com.gnu.mojadol.repository.UserRepository;
import com.gnu.mojadol.service.MailService;
import com.gnu.mojadol.service.TokenService;
import com.gnu.mojadol.service.UserService;
import com.gnu.mojadol.service.impl.CustomUserDetailServiceImpl;
import com.gnu.mojadol.service.impl.KakaoServiceImpl;
import com.gnu.mojadol.service.impl.UserServiceImpl;
import com.gnu.mojadol.utils.JwtUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.Random;
@Tag(name = "인증", description = "토큰이 필요 없는 기능 API")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserService userService;

    @Autowired
    private KakaoServiceImpl kakaoService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private MailService mailService;

    //로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequestDto authRequestDto) throws Exception {
        System.out.println("AuthController login " + new Date());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequestDto.getUserId(), authRequestDto.getUserPw())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateAccessToken(userDetails.getUsername());
            String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername());

            // Redis에 토큰 저장
            tokenService.saveToken(userDetails.getUsername(), refreshToken, 120, TimeUnit.MINUTES);
            AuthResponseDto responseDto = new AuthResponseDto();
            responseDto.setAccessToken(token);

            User user = userRepository.findByUserId(authRequestDto.getUserId());

            HttpHeaders headers = new HttpHeaders();
            headers.set("accessToken", "Bearer " + responseDto.getAccessToken());
            headers.set("userSeq", String.valueOf(user.getUserSeq()));
            return new ResponseEntity<>("YES", headers, HttpStatus.OK); // 클라이언트에 토큰 반환
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("아이디 혹은 비밀번호를 확인해주세요.");
        }
    }

    //로그아웃
    @PostMapping("/logout")
    public String logout(@RequestHeader("Authorization") String accessToken) {
        System.out.println("AuthController logout " + new Date());
        System.out.println(accessToken);
        tokenService.deleteToken(accessToken);
        return "YES";
    }

    //회원가입
    @PostMapping("/addUser")
    public ResponseEntity<?> register(@RequestBody UserRequestDto userRequestDto) {
        System.out.println("AuthController register " + new Date());
        try {
            userService.addUser(userRequestDto);
            return ResponseEntity.ok("YES");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok("NO");
        }
    }

    @GetMapping("/kakao/login")
    public ResponseEntity<?> kakaoLogin() {
        System.out.println("AuthController kakaoLogin " + new Date());
        String location = "https://kauth.kakao.com/oauth/authorize?client_id="+ clientId + "&redirect_uri=" + redirectUri + "&response_type=code&prompt=login";
        Map<String, String> response = new HashMap<>();
        response.put("location", location);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/kakao/callback")
    public ResponseEntity<?> callback(@RequestParam("code") String code) {
        System.out.println("AuthController kakaoCallback " + new Date());

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 날짜를 문자열로 변환
        String dateString = dateFormat.format(date);

        KakaoResponseDto kakaoResponseDto = kakaoService.getAccessTokenFromKakao(code);

        String kakaoAccessToken = kakaoResponseDto.accessToken;

        KakaoUserInfoResponseDto userInfo = kakaoService.getUserInfo(kakaoAccessToken);

        String userId = String.valueOf(userInfo.getId());

        User userData = userRepository.findByUserId(userId);

        if(userData == null) {
            // 회원 정보 DB에 저장
            User user = new User();
            user.setUserId(userId);
            user.setNickname(userInfo.getKakaoAccount().getProfile().getNickName());
            //user.setProfileImageUrl(userInfo.getKakaoAccount().getProfile().getProfileImageUrl());
            user.setUserName(userInfo.getKakaoAccount().getProfile().getNickName());
            user.setRegiDate(dateString);
            // 임시로 비밀번호 email로 받게함
            user.setUserPw(userInfo.getKakaoAccount().email);

            userRepository.save(user);
            System.out.println("KakaoUser data Save Success");
        }

        String accessToken = jwtUtil.generateAccessToken(userData.getUserId());
        String refreshToken = jwtUtil.generateRefreshToken(userData.getUserId());

        tokenService.saveToken(userData.getUserId(), refreshToken, 120, TimeUnit.MINUTES);

        HttpHeaders headers = new HttpHeaders();
        System.out.println(userData.getUserSeq());

        String redirectUrl = "http://10.0.2.2:8081/Board?accessToken=" + accessToken + "&userSeq=" + userData.getUserSeq();
        headers.setLocation(URI.create(redirectUrl));
        return new ResponseEntity<>(null, headers, HttpStatus.FOUND);
    }

    //아이디 찾기
    @PostMapping("/findUserId")
    public ResponseEntity<?> findUserId(@RequestBody UserRequestDto userRequestDto) {
        System.out.println("AuthController findUserId" + new Date());

        try {
            User user = userRepository.findByMail(userRequestDto.getMail());

            if (user != null) {

                String userId = user.getUserId();
                if (userId.length() > 2) {
                    userId = userId.substring(0, userId.length() - 2) + "**";
                }

                String message = "";

                message += "<!DOCTYPE html>";
                message += "<html lang='en'>";
                message += "<head>";
                message += "    <meta charset='UTF-8'>";
                message += "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>";
                message += "    <style>";
                message += "        body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f9f9f9; }";
                message += "        .email-container { max-width: 600px; margin: 30px auto; background: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); }";
                message += "        .header { text-align: center; font-size: 24px; font-weight: bold; color: #333; margin-bottom: 20px; }";
                message += "        .content { font-size: 16px; color: #555; line-height: 1.5; }";
                message += "        .content p { margin: 10px 0; }";
                message += "        .content .highlight { font-weight: bold; color: #007BFF; }";
                message += "        .footer { text-align: center; margin-top: 20px; font-size: 14px; color: #aaa; }";
                message += "    </style>";
                message += "</head>";
                message += "<body>";
                message += "    <div class='email-container'>";
                message += "        <div class='header'> 추견 60분 아이디 찾기 결과 </div>";
                message += "        <div class='content'>";
                message += "            <p>안녕하세요, <span class='highlight'>" + user.getUsername() + "</span>회원님!</p>";
                message += "            <p>요청하신 아이디는 다음과 같습니다:</p>";
                message += "            <p class='highlight'>" + userId + "</p>";
                message += "            <p>아이디를 안전하게 보관하시고, 로그인 정보를 타인과 공유하지 마세요.</p>";
                message += "        </div>";
                message += "        <div class='footer'>이 메일은 발신 전용입니다. 문의 사항은 고객센터를 이용해 주세요.</div>";
                message += "    </div>";
                message += "</body>";
                message += "</html>";

                MailDto mailDto = new MailDto();
                mailDto.setTitle("추견 60분 " + user.getUsername() + "님 아이디 찾기");
                mailDto.setAddress(user.getMail());
                mailDto.setMessage(message);
                mailService.mailSend(mailDto);
            }

            return ResponseEntity.ok("YES");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("이메일이 잘못 되었습니다.");
        }
    }

    // 비밀번호 찾기
    @PostMapping("/findPassword")
    public ResponseEntity<?> findPassword(@RequestBody UserRequestDto userRequestDto) {
        System.out.println("AuthController findPassword" + new Date());
        try {
            User user = userRepository.findByUserIdAndMail(userRequestDto.getUserId(), userRequestDto.getMail());
            if (user != null) {

                Random random = new Random();
                int min = 100000;
                int max = 999999;
                String verificationCode = String.valueOf(random.nextInt(max - min + 1) + min);

                tokenService.saveToken(user.getUserId() + user.getMail(), verificationCode, 5, TimeUnit.MINUTES);

                String message = "";

                message += "<!DOCTYPE html>";
                message += "<html lang='en'>";
                message += "<head>";
                message += "    <meta charset='UTF-8'>";
                message += "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>";
                message += "    <style>";
                message += "        body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f9f9f9; }";
                message += "        .email-container { max-width: 600px; margin: 30px auto; background: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); }";
                message += "        .header { text-align: center; font-size: 24px; font-weight: bold; color: #333; margin-bottom: 20px; }";
                message += "        .content { font-size: 16px; color: #555; line-height: 1.5; }";
                message += "        .content p { margin: 10px 0; }";
                message += "        .content .highlight { font-weight: bold; color: #007BFF; }";
                message += "        .footer { text-align: center; margin-top: 20px; font-size: 14px; color: #aaa; }";
                message += "    </style>";
                message += "</head>";
                message += "<body>";
                message += "    <div class='email-container'>";
                message += "        <div class='header'> 추견 60분 메일 본인 인증 코드 </div>";
                message += "        <div class='content'>";
                message += "            <p>안녕하세요, 회원님!</p>";
                message += "            <p>추견 60분 본인 인증 번호입니다.</p>";
                message += "            <h4 class='highlight'>" + verificationCode + "</h4>";
                message += "            <p>코드를 안전하게 보관하시고, 로그인 정보를 타인과 공유하지 마세요.</p>";
                message += "        </div>";
                message += "        <div class='footer'>이 메일은 발신 전용입니다. 문의 사항은 고객센터를 이용해 주세요.</div>";
                message += "    </div>";
                message += "</body>";
                message += "</html>";

                MailDto mailDto = new MailDto();
                mailDto.setTitle("추견 60분 " + user.getUsername() + "님 아이디 찾기");
                mailDto.setAddress(user.getMail());
                mailDto.setMessage(message);
                mailService.mailSend(mailDto);
            }
            return ResponseEntity.ok("YES");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("아이디 및 이메일이 잘못되었습니다.");
        }
    }

    @PostMapping("/mailCheck")
    public ResponseEntity<?> mailCheck(@RequestBody UserRequestDto userRequestDto, @RequestBody String code){
        System.out.println("AuthController mailCheck" + new Date());
        try {
            String redisCode = tokenService.getToken(userRequestDto.getUserId() + userRequestDto.getMail());
            if (code.equals(redisCode)) {

                tokenService.deleteToken(userRequestDto.getUserId() + userRequestDto.getMail());

                return ResponseEntity.ok("YES");
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("코드가 잘못되었습니다.");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("잘못된 요청 입니다.");
        }
    }

    @PostMapping("/updatePassword")
    public ResponseEntity<?> updatePassword(@RequestBody UserRequestDto userRequestDto) {
        System.out.println("AuthController updatePassword" + new Date());
        try {
            userService.updatePassword(userRequestDto);

            return ResponseEntity.ok("YES");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("잘못된 요청 입니다.");
        }
    }

    @PostMapping("/checkId")
    public ResponseEntity<?> checkId(@RequestBody UserRequestDto userRequestDto) {
        System.out.println("AuthController checkId" + new Date());

        try {
            String check = userService.checkId(userRequestDto.getUserId());
            if (check.isEmpty()) {
                return ResponseEntity.ok("YES");
            }
            return ResponseEntity.ok("NO");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("잘못된 요청 입니다.");
        }
    }
}

