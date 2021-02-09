package com.sp.sec.user;

import com.sp.sec.user.domain.Authority;
import com.sp.sec.user.domain.User;
import com.sp.sec.user.service.UserService;
import lombok.AllArgsConstructor;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@AllArgsConstructor
public class UserTestHelper {

    private final UserService userService;

    public User createUser(String name) {
        User user = User.builder()
                .name(name)
                .email(name + "@test.com")
                .password(name + "1234")
                .enabled(true)
                .build();

        return userService.save(user);
    }

    public User createUser(String name, String... authorities) {
        User user = createUser(name);
        Stream.of(authorities).forEach(authority -> userService.addAuthority(user.getUserId(), authority));

        return user;
    }

    public void assertUser(User user, String name) {
        assertThat(user.getUserId()).isNotNull();
        assertThat(user.getCreated()).isNotNull();
        assertThat(user.getUpdated()).isNotNull();
        assertThat(user.getName()).isEqualTo(name);
        assertThat(user.getEmail()).isEqualTo(name + "@test.com");
        assertThat(user.getPassword()).isEqualTo(name + "1234");
        assertThat(user.isEnabled()).isTrue();
    }

    public void assertUser(User user, String name, String... authorities) {
        assertUser(user, name);
        assertThat(user.getAuthorities().containsAll(
                Stream.of(authorities)
                        .map(Authority::new)
                        .collect(Collectors.toSet())
        )).isTrue();
    }
}