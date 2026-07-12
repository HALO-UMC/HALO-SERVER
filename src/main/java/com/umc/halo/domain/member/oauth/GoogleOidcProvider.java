package com.umc.halo.domain.member.oauth;

import com.umc.halo.domain.member.enums.Provider;
import org.springframework.stereotype.Component;

@Component
public class GoogleOidcProvider implements OidcProvider {

    @Override
    public String verify(String providerToken) {
        return "";
    }

    @Override
    public Provider getProvider() {
        return Provider.GOOGLE;
    }
}
