package com.umc.halo.global.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class AiClient {

    private final RestClient restClient;

    public String generate(String prompt) {

        // gemini api 요청 코드

        return "생성 결과";
    }
}
