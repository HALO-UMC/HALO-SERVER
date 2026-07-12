package com.umc.halo.domain.member.oauth;

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.umc.halo.domain.member.enums.Provider;
import com.umc.halo.domain.member.exception.code.AuthErrorCode;
import com.umc.halo.global.apiPayload.exception.ProjectException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.Date;

@Component
public class KakaoOidcProvider implements OidcProvider {

    private final String clientId;
    private final String jwksUri;
    private final String issuer;
    private final OidcJwksClient oidcJwksClient;

    public KakaoOidcProvider(
            @Value("${kakao.client-id}") String clientId,
            @Value("${kakao.jwks-uri}") String jwksUri,
            @Value("${kakao.issuer}") String issuer,
            OidcJwksClient oidcJwksClient
    ) {
        this.clientId = clientId;
        this.jwksUri = jwksUri;
        this.issuer = issuer;
        this.oidcJwksClient = oidcJwksClient;
    }


    @Override
    public String verify(String providerToken) {

        try {
            SignedJWT signedJWT = SignedJWT.parse(providerToken);
            String kid = signedJWT.getHeader().getKeyID();

            if (kid == null) {
                throw new ProjectException(AuthErrorCode.INVALID_PROVIDER_TOKEN);
            }

            RSAPublicKey publicKey = oidcJwksClient.getPublicKey(jwksUri, kid);
            JWSVerifier verifier = new RSASSAVerifier(publicKey);
            
            if (!signedJWT.verify(verifier)) {
                throw new ProjectException(AuthErrorCode.INVALID_PROVIDER_TOKEN);
            }

            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            if (!issuer.equals(claims.getIssuer())) {
                throw new ProjectException(AuthErrorCode.INVALID_PROVIDER_TOKEN);
            }

            if (claims.getAudience() == null || !claims.getAudience().contains(clientId)) {
                throw new ProjectException(AuthErrorCode.INVALID_PROVIDER_TOKEN);
            }

            Date expiration = claims.getExpirationTime();

            if (expiration == null || expiration.before(new Date())) {
                throw new ProjectException(AuthErrorCode.INVALID_PROVIDER_TOKEN);
            }

            String subject = claims.getSubject();

            if (subject == null) {
                throw new ProjectException(AuthErrorCode.INVALID_PROVIDER_TOKEN);
            }

            return subject;

        } catch (ProjectException e) {
            throw e;
        } catch (Exception e) {
            throw new ProjectException(AuthErrorCode.INVALID_PROVIDER_TOKEN);
        }
    }

    @Override
    public Provider getProvider() {
        return Provider.KAKAO;
    }
}
