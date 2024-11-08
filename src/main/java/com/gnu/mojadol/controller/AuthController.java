package com.gnu.mojadol.controller;

import com.gnu.mojadol.dto.*;
import com.gnu.mojadol.entity.User;
import com.gnu.mojadol.repository.UserRepository;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
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

            HttpHeaders headers = new HttpHeaders();
            headers.set("accessToken", "Bearer " + responseDto.getAccessToken());

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
        userService.addUser(userRequestDto);
        return ResponseEntity.ok("YES");

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

        System.out.println(code);

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 날짜를 문자열로 변환
        String dateString = dateFormat.format(date);

        KakaoResponseDto kakaoResponseDto = kakaoService.getAccessTokenFromKakao(code);

        String accessToken = kakaoResponseDto.accessToken;
        String refreshToken = kakaoResponseDto.getRefreshToken();

        KakaoUserInfoResponseDto userInfo = kakaoService.getUserInfo(accessToken);

        String userId = String.valueOf(userInfo.getId());

        // Redis에 토큰 저장
        if (accessToken != null && refreshToken != null) {
            ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
            valueOps.set(userId, refreshToken);
            System.out.println(" [Kakao Service] Tokens saved in Redis");
        } else {
            System.out.println(" [Kakao Service] Tokens are null, not saving to Redis");
        }

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

        HttpHeaders headers = new HttpHeaders();

        String redirectUrl = "http://10.0.2.2:8081/Home?accessToken=" + accessToken;
        headers.setLocation(URI.create(redirectUrl)); // 리다이렉트 URL 설정
        return new ResponseEntity<>(null, headers, HttpStatus.FOUND);
    }

}

