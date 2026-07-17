package com.umc.halo.global.ai.filter;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class SensitiveDataFilter {

    private static final Pattern PHONE_PATTERN = Pattern.compile("(01[016789])-?\\d{3,4}-?\\d{4}");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    private static final Pattern SSN_PATTERN = Pattern.compile("\\d{6}-?[1-4]\\d{6}");
    private static final Pattern CARD_PATTERN = Pattern.compile("\\d{4}-?\\d{4}-?\\d{4}-?\\d{4}");
    private static final Pattern ACCOUNT_PATTERN = Pattern.compile("\\d{3,6}-\\d{2,6}-\\d{2,8}");
    private static final Pattern URL_PATTERN = Pattern.compile("(https?://|www\\.)\\S+");
    private static final Pattern ZIP_CODE_PATTERN = Pattern.compile("\\b\\d{5}\\b");

    public String mask(String text) {

        if (text == null) {
            return null;
        }

        text = PHONE_PATTERN.matcher(text).replaceAll("[전화번호]");
        text = EMAIL_PATTERN.matcher(text).replaceAll("[이메일]");
        text = SSN_PATTERN.matcher(text).replaceAll("[주민번호]");
        text = CARD_PATTERN.matcher(text).replaceAll("[카드번호]");
        text = ACCOUNT_PATTERN.matcher(text).replaceAll("[계좌번호]");
        text = URL_PATTERN.matcher(text).replaceAll("[URL]");
        text = ZIP_CODE_PATTERN.matcher(text).replaceAll("[우편번호]");

        return text;
    }
}
