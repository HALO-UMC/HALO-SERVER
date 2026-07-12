package com.umc.halo.domain.member.oauth;

import com.umc.halo.domain.member.enums.Provider;

public interface OidcProvider {
    String verify(String providerToken);
    Provider getProvider();
}
