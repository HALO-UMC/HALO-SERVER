package com.umc.halo.global.ai.filter;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * SensitiveDataFilter.mask()의 패턴별(전화번호/이메일/주민번호/카드번호/계좌번호/URL/우편번호) 마스킹 검증.
 * 패턴이 phone -> email -> ssn -> card -> account -> url -> zip 순서로 순차 적용되므로,
 * 각 테스트는 다른 패턴과 우연히 겹치지 않는 값으로 구성했다.
 */
class SensitiveDataFilterTest {

    private final SensitiveDataFilter sensitiveDataFilter = new SensitiveDataFilter();

    @Test
    void mask는_null이_입력되면_null을_반환한다() {
        assertThat(sensitiveDataFilter.mask(null)).isNull();
    }

    @Test
    void mask는_민감정보가_없으면_원문을_그대로_반환한다() {
        String text = "오늘은 날씨가 좋아서 산책을 다녀왔어요.";

        assertThat(sensitiveDataFilter.mask(text)).isEqualTo(text);
    }

    @Test
    void mask는_전화번호를_마스킹한다() {
        assertThat(sensitiveDataFilter.mask("제 번호는 010-1234-5678이에요"))
                .isEqualTo("제 번호는 [전화번호]이에요");
    }

    @Test
    void mask는_이메일을_마스킹한다() {
        assertThat(sensitiveDataFilter.mask("연락처는 test@example.com 입니다"))
                .isEqualTo("연락처는 [이메일] 입니다");
    }

    @Test
    void mask는_주민번호를_마스킹한다() {
        assertThat(sensitiveDataFilter.mask("주민번호는 990101-1234567 입니다"))
                .isEqualTo("주민번호는 [주민번호] 입니다");
    }

    @Test
    void mask는_카드번호를_마스킹한다() {
        assertThat(sensitiveDataFilter.mask("카드번호는 1234-5678-9012-3456 이에요"))
                .isEqualTo("카드번호는 [카드번호] 이에요");
    }

    @Test
    void mask는_계좌번호를_마스킹한다() {
        assertThat(sensitiveDataFilter.mask("계좌번호는 110-123-456789 입니다"))
                .isEqualTo("계좌번호는 [계좌번호] 입니다");
    }

    @Test
    void mask는_URL을_마스킹한다() {
        assertThat(sensitiveDataFilter.mask("여기 참고하세요 https://example.com/page"))
                .isEqualTo("여기 참고하세요 [URL]");
    }

    @Test
    void mask는_5자리_우편번호를_마스킹한다() {
        assertThat(sensitiveDataFilter.mask("우편번호는 12345 입니다"))
                .isEqualTo("우편번호는 [우편번호] 입니다");
    }

    @Test
    void mask는_5자리가_아닌_일반_숫자는_그대로_둔다() {
        String text = "번호표 123456번 손님";

        assertThat(sensitiveDataFilter.mask(text)).isEqualTo(text);
    }

    @Test
    void mask는_한_문장에_여러_민감정보가_있으면_모두_마스킹한다() {
        String text = "제 번호 010-1234-5678이고 이메일은 test@example.com이에요";

        assertThat(sensitiveDataFilter.mask(text))
                .isEqualTo("제 번호 [전화번호]이고 이메일은 [이메일]이에요");
    }
}