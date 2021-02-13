package com.sq.sec.web.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class JWTUtil {

    public static final String AUTH_HEADER = "Authentication";
    public static final String REFRESH_HEADER = "refresh-token";
    public static final String BEARER = "Bearer ";

    private final Algorithm algorithm;

    private final SpJwtProperties properties;

    public JWTUtil(SpJwtProperties properties) {
        this.properties = properties;
        this.algorithm = Algorithm.HMAC512(this.properties.getSecret());
    }

    public String generate(Long userId) {
        return generate(userId, UserLogin.Type.login);
    }

    public String generate(Long userId, UserLogin.Type type) {
        return JWT.create()
                .withSubject(String.valueOf(userId))
                .withClaim("exp", Instant.now().getEpochSecond() + getLifeTime(type))
                .sign(algorithm);
    }

    private long getLifeTime(UserLogin.Type type) {
        switch (type) {
            case refresh:
                return properties.getTokenRefreshTime();
            case login:
            default:
                return properties.getTokenLifeTime();
        }
    }

    public VerifyResult verify(String token) {
        try {
            DecodedJWT decode = JWT.require(algorithm).build().verify(token);
            return VerifyResult.builder()
                    .userId(Long.parseLong(decode.getSubject()))
                    .result(true)
                    .build();
        } catch (JWTVerificationException e) {
            DecodedJWT decode = JWT.decode(token);
            return VerifyResult.builder()
                    .userId(Long.parseLong(decode.getSubject()))
                    .result(false)
                    .build();
        }
    }

}