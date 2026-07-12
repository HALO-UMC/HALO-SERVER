package com.umc.halo.domain.member.oauth;

import com.umc.halo.domain.member.enums.Provider;
import com.umc.halo.domain.member.exception.code.AuthErrorCode;
import com.umc.halo.global.apiPayload.exception.ProjectException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class OidcProviderFactory {

    private final Map<Provider, OidcProvider> providers;

    public OidcProviderFactory(List<OidcProvider> oidcProviders) {
        this.providers = oidcProviders.stream()
                .collect(Collectors.toMap(
                        OidcProvider::getProvider,
                        provider->provider
                ));
    }

    public OidcProvider getProvider(Provider provider) {
        OidcProvider oidcProvider = providers.get(provider);

        if (oidcProvider == null) {
            throw new ProjectException(AuthErrorCode.UNSUPPORTED_PROVIDER);
        }

        return oidcProvider;
    }
}
