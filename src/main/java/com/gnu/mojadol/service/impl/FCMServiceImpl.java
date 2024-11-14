package com.gnu.mojadol.service.impl;

import com.gnu.mojadol.service.FCMService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FCMServiceImpl implements FCMService {

    @Autowired
    private FirebaseMessaging firebaseMessaging;

    @Autowired
    private StringRedisTemplate redisTemplate;

    // 토큰 저장
    public void saveToken(int userId, String token) {
        String key = "user:" + userId + ":token";
        redisTemplate.opsForValue().set(key, token);
    }

    // 토큰 조회
    public String getToken(int userId) {
        String key = "user:" + userId + ":token";
        return redisTemplate.opsForValue().get(key);
    }

    // 토큰 삭제
    public void deleteToken(int userId) {
        String key = "user:" + userId + ":token";
        redisTemplate.delete(key);
    }



    // 여러 사용자에게 알림 전송
    public void sendNotificationToUsers(List<Integer> userSeqs, String title, List<String> bodies) {
        List<String> tokens = new ArrayList<>();

        if (userSeqs.size() != bodies.size()) {
            throw new IllegalArgumentException("UserSeqs와 bodies 리스트의 크기가 맞지 않습니다.");
        }

        final int BATCH_SIZE = 1000;

        // 사용자 토큰 가져오기
        for (int i = 0; i < userSeqs.size(); i++) {
            Integer userSeq = userSeqs.get(i);
            String token = getToken(userSeq);  // getToken()은 각 사용자에 대한 토큰을 가져오는 메서드
            if (token != null) {
                tokens.add(token);
            }
        }

        // 배치 크기만큼 나누어서 처리
        while (tokens.size() > 0) {
            int end = Math.min(BATCH_SIZE, tokens.size()); // 마지막 배치 크기 처리
            List<String> tokenBatch = tokens.subList(0, end);
            List<String> bodyBatch = bodies.subList(0, end);

            sendPushNotificationToMultipleTokens(tokenBatch, title, bodyBatch);

            // 처리한 후에는 그 배치를 리스트에서 제거
            tokens = tokens.subList(end, tokens.size());
            bodies = bodies.subList(end, bodies.size());
        }
    }


    private void sendPushNotificationToMultipleTokens(List<String> tokens, String title, List<String> bodies) {
        // 토큰과 본문 리스트 크기 체크
        if (tokens.size() != bodies.size()) {
            throw new IllegalArgumentException("tokens and bodies lists must have the same size");
        }

        // 사용자별로 메시지 보내기
        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            String body = bodies.get(i);  // 각 사용자의 본문

            Message message = Message.builder()
                    .setNotification(Notification.builder()
                            .setTitle(title)  // 제목 설정
                            .setBody(body)    // 본문 설정
                            .build())
                    .setToken(token)  // 각 사용자 토큰 설정
                    .build();

            // FCM 메시지 전송
            try {
                firebaseMessaging.send(message);  // 한 명에게 전송
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
