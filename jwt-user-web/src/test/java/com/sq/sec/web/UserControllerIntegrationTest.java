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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
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
public class UserControllerIntegrationTest {

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
    private User ADMIN;

    RestTemplate restTemplate = new RestTemplate();

    private URI uri(String path) throws URISyntaxException {
        return new URI(format("http://localhost:%d%s", port, path));
    }

    @BeforeEach
    void beforeEach() {
        userService.clearUsers();

        this.userTestHelper = new UserTestHelper(userService, passwordEncoder);
        this.USER = userTestHelper.createUser("user1");
        this.ADMIN = userTestHelper.createAdmin("admin");
    }

    private String getToken(String username, String password) throws URISyntaxException {
        UserLogin login = UserLogin.builder()
                .username(username)
                .password(password)
                .build();

        HttpEntity<UserLogin> entity = new HttpEntity<>(login);

        // when
        ResponseEntity<String> response = restTemplate.exchange(uri("/login"), HttpMethod.POST, entity, String.class);

        // then
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        return response.getHeaders().get(JWTUtil.AUTH_HEADER).get(0).substring(JWTUtil.BEARER.length());
    }

    @Test
    @DisplayName("1-1. user1 은 자신의 정보를 조회할 수 있다.")
    void test_1_1() throws URISyntaxException {
        String accessToken = getToken("user1@test.com", "user11234");

        ResponseEntity<User> response = restTemplate.exchange(uri("/user/" + USER.getId()),
                HttpMethod.GET, getAuthHeaderEntity(accessToken), User.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        userTestHelper.assertUser(response.getBody(), "user1");
    }

    @Test
    @DisplayName("1. admin은 user list 를 가져올 수 있다.")
    void test_get_list_admin() throws URISyntaxException {
        String accessToken = getToken("admin@test.com", "admin1234");

        ResponseEntity<String> response = restTemplate.exchange(uri("/user/list"), HttpMethod.GET, getAuthHeaderEntity(accessToken), String.class);

        System.out.println("response.getBody() = " + response.getBody());
    }

    @Test
    @DisplayName("2. user는 user list 를 가져올 수 없다.")
    void test_get_list_user() throws URISyntaxException {
        String accessToken = getToken("user1@test.com", "user11234");

        assertThatThrownBy(() -> {
            ResponseEntity<String> response = restTemplate.exchange(uri("/user/list"), HttpMethod.GET, getAuthHeaderEntity(accessToken), String.class);
            System.out.println("response.getBody() = " + response.getBody());
        })
                .isInstanceOf(HttpClientErrorException.class)
                .hasMessageContaining("403");
    }

    @Test
    @DisplayName("3. user1 에게 admin 권한을 준다.")
    void test_add_auth() {

    }

    private HttpEntity<Object> getAuthHeaderEntity(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWTUtil.AUTH_HEADER, JWTUtil.BEARER + accessToken);
        return new HttpEntity<>(headers);
    }

}
