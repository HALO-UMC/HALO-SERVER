package com.umc.halo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class HaloServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(HaloServerApplication.class, args);
    }
}