package com.umc.halo.global.ai;

import com.umc.halo.global.ai.exception.AiException;
import com.umc.halo.global.ai.exception.code.AiErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

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

        try {
            AiResponse response = restClient.post()
                    .uri(uriBuilder ->
                            uriBuilder
                                    .path("/v1beta/models/{model}:generateContent")
                                    .build(model)
                    )
                    .header("x-goog-api-key", apiKey)
                    .body(request)
                    .retrieve()
                    .body(AiResponse.class);

            if (response == null) {
                throw new AiException(AiErrorCode.AI_RESPONSE_EMPTY);
            }

            return response.getText();
        } catch (RestClientException e) {
            throw new AiException(AiErrorCode.AI_GENERATE_FAILED, e);
        }
    }
}
