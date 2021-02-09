package com.sq.sec.web;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

public class JWTTokenTest {

    @Test
    @DisplayName("1. JWT 토큰이 잘 만들어 진다.")
    void test_() {
        String token = JWT.create()
                .withSubject("min")
                .withClaim("exp", Instant.now().getEpochSecond() + 3)
                .withArrayClaim("role", new String[]{"ROLE_ADMIN", "ROLE_USER"})
                .sign(Algorithm.HMAC256("hello"));

        System.out.println("token = " + token);

        DecodedJWT decode = JWT.require(Algorithm.HMAC256("hello"))
                .build()
                .verify(token);
    }

}
