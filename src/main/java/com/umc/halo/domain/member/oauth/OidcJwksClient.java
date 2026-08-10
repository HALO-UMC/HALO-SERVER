package com.umc.halo.domain.member.oauth;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.SecurityContext;
import com.umc.halo.domain.member.exception.code.AuthErrorCode;
import com.umc.halo.global.apiPayload.exception.ProjectException;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OidcJwksClient {

    private final ConcurrentHashMap<String, RemoteJWKSet<SecurityContext>> jwkSources = new ConcurrentHashMap<>();

    public RSAPublicKey getPublicKey(String jwksUri, String kid) {

        try {
            JWKSource<SecurityContext> jwkSource = jwkSources.computeIfAbsent(
                    jwksUri,
                    uri -> new RemoteJWKSet<>(
                            createURL(uri)
                    )
            );

            JWKSelector selector = new JWKSelector(new JWKMatcher.Builder()
                    .keyID(kid)
                    .build()
            );

            List<JWK> jwks = jwkSource.get(selector, null);

            if (jwks.isEmpty()) {
                throw new ProjectException(AuthErrorCode.INVALID_PROVIDER_TOKEN);
            }

            return jwks.get(0).toRSAKey().toRSAPublicKey();

        } catch (ProjectException e) {
            throw e;
        } catch (Exception e) {
            throw new ProjectException(AuthErrorCode.INVALID_PROVIDER_TOKEN, e);
        }
    }

    private URL createURL(String uri) {
        try {
            return new URL(uri);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
