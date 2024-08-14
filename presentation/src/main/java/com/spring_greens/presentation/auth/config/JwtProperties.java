package com.spring_greens.presentation.auth.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Setter
@Getter
@Component
@ConfigurationProperties("jwt")
public class JwtProperties {
    private String issuer;
    private String secretKey;
    private int AccessTokenExpiration;
    private int RefreshTokenExpiration;

    public SecretKey getSecretKey() {
//        byte[] keyBytes = Decoders.BASE64.decode(this.secretKey);
//        return new SecretKeySpec(secretKey.getBytes(), "HmacSHA512");
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}




