package com.sq.sec.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sq.sec.web.config.UserLogin;
import com.sq.sec.web.domain.User;
import com.sq.sec.web.security.CustomUserService;
import com.sq.sec.web.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityManager;
import java.net.URI;
import java.net.URISyntaxException;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JWTLoginFilterTest {

    @LocalServerPort
    private int port;

    @Autowired
    EntityManager em;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    CustomUserService customUserService;

    @Autowired
    UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    RestTemplate restTemplate = new RestTemplate();

    private URI uri(String path) throws URISyntaxException {
        return new URI(format("http://localhost:%d%s", port, path));
    }

    @Test
    @DisplayName("1. JWT 로 로그인을 시도한다.")
    @Rollback(false)
    void test_() throws URISyntaxException {
        // given
        String email = "user1@test.com";

        User user1 = User.builder()
                .name("user1")
                .email(email)
                .password(passwordEncoder.encode("1234"))
                .build();

        userService.save(user1, "ROLE_USER");

        UserLogin login = UserLogin.builder()
                .username(email)
                .password("1234")
                .build();

        HttpEntity<UserLogin> body = new HttpEntity<>(login);

        // when
        ResponseEntity<String> response = restTemplate.exchange(uri("/login"), HttpMethod.POST, body, String.class);

        // then
        assertThat(response.getStatusCodeValue()).isEqualTo(200);

        System.out.println(response.getHeaders().get("Authentication"));
    }

}