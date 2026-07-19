package com.umc.halo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class HaloServerApplication {

    public static void main(String[] args) {
        var context = SpringApplication.run(HaloServerApplication.class, args);

        // ===== 임시: 테스트 토큰 (확인 후 삭제) =====
        var jwtUtil = context.getBean(com.umc.halo.global.security.JwtUtil.class);
        String token = jwtUtil.createAccessToken(3L);
        System.out.println("===== 테스트 토큰 =====");
        System.out.println(token);
        System.out.println("=====================");
    }

}
