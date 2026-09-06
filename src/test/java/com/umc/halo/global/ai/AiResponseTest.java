package com.umc.halo.global.ai;

import com.umc.halo.global.ai.exception.AiException;
import com.umc.halo.global.ai.exception.code.AiErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * AiResponse.getText()가 Gemini 응답 구조(candidates -> content -> parts)를
 * 어떻게 검증하고 텍스트로 합치는지 검증.
 * candidates/content/parts는 private 필드고 별도 생성자/빌더가 없어서
 * 리플렉션(ReflectionTestUtils)으로 직접 값을 채워 테스트용 객체를 만든다.
 */
class AiResponseTest {

    private AiResponse.Part part(String text) {
        AiResponse.Part part = new AiResponse.Part();
        ReflectionTestUtils.setField(part, "text", text);
        return part;
    }

    private AiResponse.Content content(List<AiResponse.Part> parts) {
        AiResponse.Content content = new AiResponse.Content();
        ReflectionTestUtils.setField(content, "parts", parts);
        return content;
    }

    private AiResponse.Candidate candidate(AiResponse.Content content) {
        AiResponse.Candidate candidate = new AiResponse.Candidate();
        ReflectionTestUtils.setField(candidate, "content", content);
        return candidate;
    }

    private AiResponse response(List<AiResponse.Candidate> candidates) {
        AiResponse response = new AiResponse();
        ReflectionTestUtils.setField(response, "candidates", candidates);
        return response;
    }

    @Test
    void getText는_candidates가_null이면_AI_RESPONSE_INVALID_예외를_던진다() {
        AiResponse response = response(null);

        assertThatThrownBy(response::getText)
                .isInstanceOf(AiException.class)
                .satisfies(e -> assertThat(((AiException) e).getErrorCode()).isEqualTo(AiErrorCode.AI_RESPONSE_INVALID));
    }

    @Test
    void getText는_candidates가_비어있으면_AI_RESPONSE_INVALID_예외를_던진다() {
        AiResponse response = response(List.of());

        assertThatThrownBy(response::getText)
                .isInstanceOf(AiException.class)
                .satisfies(e -> assertThat(((AiException) e).getErrorCode()).isEqualTo(AiErrorCode.AI_RESPONSE_INVALID));
    }

    @Test
    void getText는_content가_null이면_AI_RESPONSE_INVALID_예외를_던진다() {
        AiResponse response = response(List.of(candidate(null)));

        assertThatThrownBy(response::getText)
                .isInstanceOf(AiException.class)
                .satisfies(e -> assertThat(((AiException) e).getErrorCode()).isEqualTo(AiErrorCode.AI_RESPONSE_INVALID));
    }

    @Test
    void getText는_parts가_null이면_AI_RESPONSE_INVALID_예외를_던진다() {
        AiResponse response = response(List.of(candidate(content(null))));

        assertThatThrownBy(response::getText)
                .isInstanceOf(AiException.class)
                .satisfies(e -> assertThat(((AiException) e).getErrorCode()).isEqualTo(AiErrorCode.AI_RESPONSE_INVALID));
    }

    @Test
    void getText는_parts가_비어있으면_AI_RESPONSE_INVALID_예외를_던진다() {
        AiResponse response = response(List.of(candidate(content(List.of()))));

        assertThatThrownBy(response::getText)
                .isInstanceOf(AiException.class)
                .satisfies(e -> assertThat(((AiException) e).getErrorCode()).isEqualTo(AiErrorCode.AI_RESPONSE_INVALID));
    }

    @Test
    void getText는_모든_part의_텍스트가_공백이면_AI_RESPONSE_INVALID_예외를_던진다() {
        AiResponse response = response(List.of(candidate(content(List.of(part(""), part(null))))));

        assertThatThrownBy(response::getText)
                .isInstanceOf(AiException.class)
                .satisfies(e -> assertThat(((AiException) e).getErrorCode()).isEqualTo(AiErrorCode.AI_RESPONSE_INVALID));
    }

    @Test
    void getText는_여러_part의_텍스트를_이어붙여_반환한다() {
        AiResponse response = response(List.of(
                candidate(content(List.of(part("안녕하세요. "), part(null), part("좋은 하루 되세요."))))
        ));

        String result = response.getText();

        assertThat(result).isEqualTo("안녕하세요. 좋은 하루 되세요.");
    }
}