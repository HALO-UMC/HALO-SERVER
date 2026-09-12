package com.umc.halo.global.config;

import com.umc.halo.global.apiPayload.exception.ProjectException;
import io.sentry.Sentry;
import io.sentry.SentryOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SentryConfig {

    @Bean
    public Sentry.OptionsConfiguration<SentryOptions> sentryOptionsOptionsConfiguration() {
        return options -> options.setBeforeSend((sentryEvent, hint) -> {
            if (sentryEvent.getThrowable() instanceof ProjectException projectException
                    && projectException.getErrorCode().getStatus().is4xxClientError()) {
                return null;
            }
            return sentryEvent;
        });
    }
}
