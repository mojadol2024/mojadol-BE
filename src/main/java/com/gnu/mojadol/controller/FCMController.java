package com.gnu.mojadol.controller;

import com.gnu.mojadol.dto.NotificationRequestDto;
import com.gnu.mojadol.entity.User;
import com.gnu.mojadol.repository.UserRepository;
import com.gnu.mojadol.service.FCMService;
import com.gnu.mojadol.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/auth/FCM")
public class FCMController {

    @Autowired
    private FCMService fcmService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/save")
    public String sendNotification(@RequestBody NotificationRequestDto request) {
        System.out.println("FCMController save" + new Date());
        try {
            User user = userRepository.findByUserId(request.getUserId());

            fcmService.saveToken(user.getUserSeq(), request.getToken());

            return "Notification sent successfully";
        } catch (Exception e) {
            return "Failed to send notification: " + e.getMessage();
        }
    }
}
