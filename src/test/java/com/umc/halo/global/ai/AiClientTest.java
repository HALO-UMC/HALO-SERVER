package com.umc.halo.global.ai;

import com.umc.halo.global.ai.exception.AiException;
import com.umc.halo.global.ai.exception.code.AiErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

/**
 * AiClient.generate()가 RestClient 응답을 어떻게 처리/변환하는지 검증.
 * RestClient는 fluent(메서드 체이닝) API라 각 단계(post -> uri -> header -> body -> retrieve -> body)를
 * 전부 Mockito mock으로 이어붙여서 체이닝을 재현한다.
 */
@ExtendWith(MockitoExtension.class)
class AiClientTest {

    @Mock
    private RestClient restClient;
    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock
    private RestClient.RequestBodySpec requestBodySpec;
    @Mock
    private RestClient.ResponseSpec responseSpec;
    @Mock
    private AiResponse aiResponse;

    private AiClient aiClient;

    @BeforeEach
    void setUp() {
        aiClient = new AiClient(restClient);
        // apiKey/model은 @Value로 주입되는 필드라 생성자에 없음 -> 리플렉션으로 직접 세팅
        ReflectionTestUtils.setField(aiClient, "apiKey", "test-api-key");
        ReflectionTestUtils.setField(aiClient, "model", "gemini-3.5-flash-lite");
    }

    // post() ~ retrieve()까지의 체이닝을 공통으로 스텁
    private void stubChainUpToRetrieve() {
        given(restClient.post()).willReturn(requestBodyUriSpec);
        given(requestBodyUriSpec.uri(any(Function.class))).willReturn(requestBodySpec);
        given(requestBodySpec.header(anyString(), anyString())).willReturn(requestBodySpec);
        given(requestBodySpec.body(any(AiRequest.class))).willReturn(requestBodySpec);
        given(requestBodySpec.retrieve()).willReturn(responseSpec);
    }

    @Test
    void generate는_정상_응답이면_AiResponse의_텍스트를_반환한다() {
        given(aiResponse.getText()).willReturn("생성된 요약");
        stubChainUpToRetrieve();
        given(responseSpec.body(AiResponse.class)).willReturn(aiResponse);

        String result = aiClient.generate("프롬프트");

        assertThat(result).isEqualTo("생성된 요약");
    }

    @Test
    void generate는_응답이_null이면_AI_RESPONSE_EMPTY_예외를_던진다() {
        stubChainUpToRetrieve();
        given(responseSpec.body(AiResponse.class)).willReturn(null);

        assertThatThrownBy(() -> aiClient.generate("프롬프트"))
                .isInstanceOf(AiException.class)
                .satisfies(e -> assertThat(((AiException) e).getErrorCode()).isEqualTo(AiErrorCode.AI_RESPONSE_EMPTY));
    }

    @Test
    void generate는_RestClientException이_발생하면_AI_GENERATE_FAILED로_변환한다() {
        stubChainUpToRetrieve();
        given(responseSpec.body(AiResponse.class)).willThrow(new RestClientException("연결 실패"));

        assertThatThrownBy(() -> aiClient.generate("프롬프트"))
                .isInstanceOf(AiException.class)
                .satisfies(e -> assertThat(((AiException) e).getErrorCode()).isEqualTo(AiErrorCode.AI_GENERATE_FAILED));
    }
}