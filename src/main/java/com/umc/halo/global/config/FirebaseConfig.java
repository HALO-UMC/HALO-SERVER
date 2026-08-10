package com.umc.halo.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

@Configuration
public class FirebaseConfig {

    @Value("${FIREBASE_SERVICE_ACCOUNT_B64:}")
    private String serviceAccountB64;

    @PostConstruct
    public void initialize() throws IOException {

        if (serviceAccountB64 == null || serviceAccountB64.isBlank()) {
            throw new IllegalStateException("FIREBASE_SERVICE_ACCOUNT_B64 환경변수가 설정되지 않았습니다.");
        }

        try (InputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(serviceAccountB64))) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(inputStream))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        }
    }
}
