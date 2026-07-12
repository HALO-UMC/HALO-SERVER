package com.umc.halo.domain.member.oauth;

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.umc.halo.domain.member.enums.Provider;
import com.umc.halo.domain.member.exception.code.AuthErrorCode;
import com.umc.halo.global.apiPayload.exception.ProjectException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPublicKey;
import java.util.Date;

@Component
public class KakaoOidcProvider extends AbstractOidcProvider {

    private final String clientId;
    private final String jwksUri;
    private final String issuer;

    public KakaoOidcProvider(
            @Value("${kakao.client-id}") String clientId,
            @Value("${kakao.jwks-uri}") String jwksUri,
            @Value("${kakao.issuer}") String issuer,
            OidcJwksClient oidcJwksClient
    ) {
        super(oidcJwksClient);
        this.clientId = clientId;
        this.jwksUri = jwksUri;
        this.issuer = issuer;
    }

    @Override
    protected String getClientId() {
        return clientId;
    }

    @Override
    protected String getIssuer() {
        return issuer;
    }

    @Override
    protected String getJwksUri() {
        return jwksUri;
    }

    @Override
    public Provider getProvider() {
        return Provider.KAKAO;
    }
}
