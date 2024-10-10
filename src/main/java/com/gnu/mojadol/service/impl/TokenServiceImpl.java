package com.gnu.mojadol.service.impl;

import com.gnu.mojadol.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TokenServiceImpl implements TokenService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void saveToken(String key, String token, long duration, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, token, duration, unit);
    }
    @Override
    public String getToken(String key) {
        return redisTemplate.opsForValue().get(key);
    }
    @Override
    public void deleteToken(String key) {
        redisTemplate.delete(key);
    }
}
