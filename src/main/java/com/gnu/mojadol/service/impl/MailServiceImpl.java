package com.gnu.mojadol.service.impl;

import com.gnu.mojadol.dto.MailDto;
import com.gnu.mojadol.service.MailService;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public void mailSend(MailDto mailDto) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(mailDto.getAddress());
            helper.setSubject(mailDto.getTitle());
            helper.setText(mailDto.getMessage(), true);
            javaMailSender.send(message);

        }catch (jakarta.mail.MessagingException e) {
            e.printStackTrace(); // 에러 출력
            throw new RuntimeException("메일 전송 실패: " + e.getMessage());
        }
    }
}
