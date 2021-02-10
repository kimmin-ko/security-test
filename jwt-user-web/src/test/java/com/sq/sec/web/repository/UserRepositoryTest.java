package com.sq.sec.web.repository;

import com.sq.sec.web.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("")
    void test_() {
        // given
        User user1 = new User("user1@test.com", "user1", "1234");

        // when
        User savedUser = userRepository.save(user1);

        System.out.println("savedUser = " + savedUser);

        // then

    }

}