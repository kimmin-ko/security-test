package com.sq.sec.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sq.sec.web.config.JWTUtil;
import com.sq.sec.web.config.UserLogin;
import com.sq.sec.web.domain.Authority;
import com.sq.sec.web.domain.User;
import com.sq.sec.web.security.CustomUserService;
import com.sq.sec.web.service.UserService;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityManager;
import java.net.URI;
import java.net.URISyntaxException;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    private UserTestHelper userTestHelper;

    private User USER;

    RestTemplate restTemplate = new RestTemplate();

    private URI uri(String path) throws URISyntaxException {
        return new URI(format("http://localhost:%d%s", port, path));
    }

    @BeforeEach
    void beforeEach() {
        userService.clearUsers();

        this.userTestHelper = new UserTestHelper(userService, passwordEncoder);
        this.USER = userTestHelper.createUser("user1");
    }

    @Test
    @DisplayName("1. JWT 로 로그인을 시도한다.")
    @Rollback(false)
    void test_() throws URISyntaxException {
        // given
        UserLogin login = UserLogin.builder()
                .username("user1@test.com")
                .password("user11234")
                .build();

        HttpEntity<UserLogin> body = new HttpEntity<>(login);

        // when
        ResponseEntity<String> response = restTemplate.exchange(uri("/login"), HttpMethod.POST, body, String.class);

        // then
        System.out.println(response.getHeaders().get(JWTUtil.AUTH_HEADER));
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    @DisplayName("2. 비밀번호가 틀리면 로그인하지 못한다.")
    void test_2() throws URISyntaxException {
        UserLogin userLogin = UserLogin.builder()
                .username("user1@test.com")
                .password("user1123")
                .build();

        HttpEntity<UserLogin> entity = new HttpEntity<>(userLogin);

        assertThatThrownBy(() -> restTemplate.exchange(uri("/login"), HttpMethod.POST, entity, String.class))
                .isInstanceOf(HttpClientErrorException.class)
                .hasMessageContaining("401");
    }

}