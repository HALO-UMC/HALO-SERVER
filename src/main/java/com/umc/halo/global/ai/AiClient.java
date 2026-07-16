package com.umc.halo.global.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class AiClient {

    private final @Qualifier("aiRestClient") RestClient restClient;

    @Value("${gemini.api-key}")
    private String apiKey;

    @Value("${gemini.model}")
    private String model;

    public String generate(String prompt) {

        AiRequest request = AiRequest.from(prompt);

        AiResponse response = restClient.post()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/v1beta/models/{model}:generateContent")
                                .queryParam("key", apiKey)
                                .build(model)
                )
                .body(request)
                .retrieve()
                .body(AiResponse.class);

        if (response == null) {
            throw new IllegalStateException("Gemini 응답이 없습니다.");
        }

        return response.getText();
    }
}
