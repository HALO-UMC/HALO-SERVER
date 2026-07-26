package com.umc.halo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class HaloServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(HaloServerApplication.class, args);
    }
}
