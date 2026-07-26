package com.umc.halo.domain.member.oauth;

public record OidcUserInfo(
        String providerId,
        String email
) {}
