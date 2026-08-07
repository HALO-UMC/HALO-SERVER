package com.umc.halo.domain.notification.service;

import com.google.firebase.messaging.*;
import com.umc.halo.domain.member.entity.MemberDevice;
import com.umc.halo.domain.member.repository.MemberDeviceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FcmService {

    private final MemberDeviceRepository memberDeviceRepository;

    public boolean send(MemberDevice memberDevice, String title, String body) {
        Notification notification = Notification.builder().setTitle(title).setBody(body).build();

        Message message = Message.builder().setToken(memberDevice.getFcmToken()).setNotification(notification).build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("FCM 전송 성공 : response={}", response);
            return true;
        } catch (FirebaseMessagingException e) {
            if (e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED) {
                log.info("만료된 FCM 토큰 삭제 : deviceId={}", memberDevice.getId());
                memberDeviceRepository.delete(memberDevice);
            }
            log.error("FCM 전송 실패 deviceId={}, errorCode={}", memberDevice.getId(), e.getMessagingErrorCode(), e);
            return false;
        } catch (Exception e) {
            log.error("FCM 전송 실패 deviceId={}", memberDevice.getId(), e);
            throw new RuntimeException(e);
        }

    }
}
