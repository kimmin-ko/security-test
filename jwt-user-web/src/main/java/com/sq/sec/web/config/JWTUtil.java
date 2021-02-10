package com.sq.sec.web.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.time.Instant;

public class JWTUtil {

    private final String secret = "hello";
    private final Algorithm algorithm = Algorithm.HMAC512(secret);
    private final long lifeTime = 30;

    public String generate(Long userId) {
        return JWT.create()
                .withSubject(String.valueOf(userId))
                .withClaim("exp", Instant.now().getEpochSecond() + lifeTime)
                .sign(algorithm);
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