package com.umc.halo.domain.member.oauth;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.umc.halo.domain.member.exception.code.AuthErrorCode;
import com.umc.halo.global.apiPayload.exception.ProjectException;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OidcJwksClient {

    private final ConcurrentHashMap<String, JWKSet> cache = new ConcurrentHashMap<>();

    public RSAPublicKey getPublicKey(String jwksUri, String kid) {

        JWKSet jwkSet = cache.computeIfAbsent(jwksUri, this::loadJwkSet);

        try {
            JWK jwk = jwkSet.getKeyByKeyId(kid);

            if (jwk == null) {
                throw new ProjectException(AuthErrorCode.INVALID_PROVIDER_TOKEN);
            }

            return jwk.toRSAKey().toRSAPublicKey();

        } catch (ProjectException e) {
            throw e;
        } catch (Exception e) {
            throw new ProjectException(AuthErrorCode.INVALID_PROVIDER_TOKEN);
        }
    }

    private JWKSet loadJwkSet(String jwksUri) {
        try {
            return JWKSet.load(new URL(jwksUri));
        } catch (Exception e) {
            throw new ProjectException(AuthErrorCode.INVALID_PROVIDER_TOKEN);
        }
    }
}
