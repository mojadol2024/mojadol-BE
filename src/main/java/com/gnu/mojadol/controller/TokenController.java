package com.gnu.mojadol.controller;

import com.gnu.mojadol.service.TokenService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.concurrent.TimeUnit;
@Tag(name = "토큰", description = "토큰관련 API")
@RestController
@RequestMapping("/token")
public class TokenController {

    @Autowired
    private TokenService tokenService;

    // 토큰 저장 API (예: 액세스 토큰)
    @PostMapping("/save")
    public String saveToken(@RequestParam String key, @RequestParam String token) {
        System.out.println("saveToken" + new Date());
        long duration = 120; // 2시간 동안 유효한 토큰
        tokenService.saveToken(key, token, duration, TimeUnit.MINUTES);
        return "Token saved!";
    }

    // 토큰 조회 API
    @GetMapping("/get")
    public String getToken(@RequestParam String key) {
        System.out.println("getToken" + new Date());
        return tokenService.getToken(key);
    }

    // 토큰 삭제 API
    @DeleteMapping("/delete")
    public String deleteToken(@RequestParam String key) {
        System.out.println("deleteToken" + new Date());
        tokenService.deleteToken(key);
        return "Token deleted!";
    }
}

