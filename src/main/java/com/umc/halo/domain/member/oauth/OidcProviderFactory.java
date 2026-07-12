package com.umc.halo.domain.member.oauth;

import com.umc.halo.domain.member.enums.Provider;
import com.umc.halo.domain.member.exception.code.AuthErrorCode;
import com.umc.halo.global.apiPayload.exception.ProjectException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class OidcProviderFactory {

    private final Map<Provider, AbstractOidcProvider> providers;

    public OidcProviderFactory(List<AbstractOidcProvider> oidcProviders) {
        this.providers = oidcProviders.stream()
                .collect(Collectors.toMap(
                        AbstractOidcProvider::getProvider,
                        Function.identity()
                ));
    }

    public AbstractOidcProvider getProvider(Provider provider) {
        AbstractOidcProvider oidcProvider = providers.get(provider);

        if (oidcProvider == null) {
            throw new ProjectException(AuthErrorCode.UNSUPPORTED_PROVIDER);
        }

        return oidcProvider;
    }
}
