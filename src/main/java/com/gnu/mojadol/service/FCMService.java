package com.gnu.mojadol.service;

import java.util.List;

public interface FCMService {

    void saveToken(int userId, String token);

    String getToken(int userId);

    void deleteToken(int userId);

    void sendNotificationToUsers(List<Integer> userIds, String title, List<String> bodies);
}
