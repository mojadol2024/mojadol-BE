package com.gnu.mojadol.controller;

import com.gnu.mojadol.dto.AuthRequestDto;
import com.gnu.mojadol.dto.AuthResponseDto;
import com.gnu.mojadol.dto.UserRequestDto;
import com.gnu.mojadol.service.TokenService;
import com.gnu.mojadol.service.UserService;
import com.gnu.mojadol.service.impl.CustomUserDetailServiceImpl;
import com.gnu.mojadol.service.impl.UserServiceImpl;
import com.gnu.mojadol.utils.JwtUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.concurrent.TimeUnit;
@Tag(name = "인증", description = "토큰이 필요 없는 기능 API")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserService userService;

    //로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequestDto authRequestDto) throws Exception {
        System.out.println("AuthController login" + new Date());
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

            return ResponseEntity.ok(responseDto); // 클라이언트에 토큰 반환
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("아이디 혹은 비밀번호를 확인해주세요.");
        }
    }

    //로그아웃
    @PostMapping("/logout")
    public String logout(@RequestParam String username) {
        System.out.println("AuthController logout" + new Date());
        // Redis에서 해당 사용자의 토큰 삭제
        tokenService.deleteToken(username);
        return "로그아웃 성공";
    }

    //회원가입
    @PostMapping("/addUser")
    public ResponseEntity<?> register(@RequestBody UserRequestDto userRequestDto) {
        System.out.println("AuthController register" + new Date());
        userService.addUser(userRequestDto);
        return ResponseEntity.ok("회원가입 성공");

    }


}

