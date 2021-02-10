package com.sq.sec.web;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Arrays;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class JWTTokenTest {

    @Test
    @DisplayName("1. JWT 토큰이 잘 만들어 진다.")
    void test_() throws InterruptedException {
        Algorithm algorithm = Algorithm.HMAC256("hello");

        String token = JWT.create()
                .withSubject("min")
                .withClaim("exp", Instant.now().getEpochSecond() + 1)
                .withArrayClaim("role", new String[]{"ROLE_ADMIN", "ROLE_USER"})
                .sign(algorithm);

        Thread.sleep(2000);
        // 유효하지 않은 토큰에 대해서도 로깅을 남길 때 등 사용할 수 있다.
//        DecodedJWT decode = JWT.decode(token);

        assertThatThrownBy(() -> JWT.require(algorithm).build().verify(token))
                .isInstanceOf(TokenExpiredException.class);
    }

    private void printClaim(String key, Claim claim) {
        if (claim.isNull()) {
            System.out.printf("%s:{nll}%s\n", key, "Claim is null.");
        } else if (claim.asBoolean() != null) {
            System.out.printf("%s:{bol}%b\n", key, claim.asBoolean());
        } else if (claim.asLong() != null) {
            System.out.printf("%s:{lng}%d\n", key, claim.asLong());
        } else if (claim.asString() != null) {
            System.out.printf("%s:{str}%s\n", key, claim.asString());
        } else if (claim.asDate() != null) {
            System.out.printf("%s:{dte}%s\n", key, claim.asDate().toString());
        } else if (claim.asInt() != null) {
            System.out.printf("%s:{int}%d\n", key, claim.asInt());
        } else if (claim.asDouble() != null) {
            System.out.printf("%s:{dbl}%f\n", key, claim.asDouble());
        } else if (claim.asArray(String.class) != null) {
            String[] claims = claim.asArray(String.class);
            System.out.printf("%s:{arr}%s\n", key, Arrays.stream(claims).collect(toList()));
        } else if (claim.asMap() != null) {
            System.out.printf("%s:{map}%s\n", key, claim.asMap());
        }
    }

}