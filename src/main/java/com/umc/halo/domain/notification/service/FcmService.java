package com.umc.halo.domain.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FcmService {

    public void send(String token, String title, String body) {
        Notification notification = Notification.builder().setTitle(title).setBody(body).build();

        Message message = Message.builder().setToken(token).setNotification(notification).build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("FCM 전송 성공 : response={}", response);
        } catch (Exception e) {
            log.error("FCM 전송 실패 token={}", token, e);
            throw new RuntimeException(e);
        }

    }
}
