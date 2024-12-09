package com.gnu.mojadol.service;

import java.util.concurrent.TimeUnit;

public interface TokenService {
    boolean saveToken(String key, String token, long duration, TimeUnit unit);
    String getToken(String key);
    void deleteToken(String key);
}
