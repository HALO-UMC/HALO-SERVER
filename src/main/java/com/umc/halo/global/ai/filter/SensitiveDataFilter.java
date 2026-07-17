package com.umc.halo.global.ai.filter;

import org.springframework.stereotype.Component;

@Component
public class SensitiveDataFilter {

    private static final String PHONE_PATTERN = "(01[016789])-?\\d{3,4}-?\\d{4}";
    private static final String EMAIL_PATTERN = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";
    private static final String SSN_PATTERN = "\\d{6}-?[1-4]\\d{6}";
    private static final String CARD_PATTERN = "\\d{4}-?\\d{4}-?\\d{4}-?\\d{4}";
    private static final String ACCOUNT_PATTERN = "\\d{3,6}-\\d{2,6}-\\d{2,8}";
    private static final String URL_PATTERN = "(https?://|www\\.)\\S+";
    private static final String ZIP_CODE_PATTERN = "\\b\\d{5}\\b";

    public String mask(String text) {

        if (text == null) {
            return null;
        }

        return text
                .replaceAll(PHONE_PATTERN, "[전화번호]")
                .replaceAll(EMAIL_PATTERN, "[이메일]")
                .replaceAll(SSN_PATTERN, "[주민번호]")
                .replaceAll(CARD_PATTERN, "[카드번호]")
                .replaceAll(ACCOUNT_PATTERN, "[계좌번호]")
                .replaceAll(URL_PATTERN, "[URL]")
                .replaceAll(ZIP_CODE_PATTERN, "[우편번호]");
    }
}
