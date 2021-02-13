package com.sq.sec.web;

import com.sq.sec.web.domain.Authority;
import com.sq.sec.web.domain.User;
import com.sq.sec.web.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class UserTestHelper {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserTestHelper(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(String name) {
        User user = User.builder()
                .name(name)
                .email(name + "@test.com")
                .password(passwordEncoder.encode(name + "1234"))
                .build();

        Authority authority = Authority.builder()
                .email(user.getEmail())
                .authority("ROLE_USER")
                .build();

        return userService.save(user, authority);
    }

    public User createAdmin(String name) {
        User user = User.builder()
                .name(name)
                .email(name + "@test.com")
                .password(passwordEncoder.encode(name + "1234"))
                .build();

        Authority authority = Authority.builder()
                .email(user.getEmail())
                .authority("ROLE_ADMIN")
                .build();

        return userService.save(user, authority);
    }

    public User createUser(String name, String... authorities) {
        User user = createUser(name);
        Stream.of(authorities).forEach(authority -> userService.addAuthority(user.getId(), authority));

        return user;
    }

    public void assertUser(User user, String name) {
        assertThat(user.getId()).isNotNull();
        assertThat(user.getCreatedDate()).isNotNull();
        assertThat(user.getUpdatedDate()).isNotNull();
        assertThat(user.getName()).isEqualTo(name);
        assertThat(user.getEmail()).isEqualTo(name + "@test.com");
//        assertThat(user.getPassword()).isEqualTo(name + "1234");
    }

}