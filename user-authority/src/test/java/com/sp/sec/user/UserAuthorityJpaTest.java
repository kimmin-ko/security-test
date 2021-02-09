package com.sp.sec.user;

import com.sp.sec.user.domain.User;
import com.sp.sec.user.repository.UserRepository;
import com.sp.sec.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@ActiveProfiles("test")
public class UserAuthorityJpaTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private UserRepository userRepository;

    private UserService userService;

    private UserTestHelper testHelper;

    @BeforeEach
    void beforeEach() {
        userRepository.deleteAll();
        this.userService = new UserService(mongoTemplate, userRepository);
        this.testHelper = new UserTestHelper(userService);
    }

    @Test
    @DisplayName("1. 사용자를 생성한다.")
    void test_1() {
        testHelper.createUser("user1");
        List<User> users = userRepository.findAll();

        assertThat(users.size()).isOne();
        testHelper.assertUser(users.get(0), "user1");
    }

    @Test
    @DisplayName("2. 사용자의 이름을 수정한다.")
    void test_2() {
        User user = testHelper.createUser("user1");

        boolean isUpdate = userService.updateUserName(user.getUserId(), "modified user name");

        User findUser = userService.findUser(user.getUserId()).get();

        assertThat(isUpdate).isTrue();
        assertThat(findUser.getName()).isEqualTo("modified user name");
    }

    @Test
    @DisplayName("3. authority를 부여한다.")
    void test_3() {
//        User user = testHelper.createUser("user1", Authority.ROLE_USER);
//        user.addAuthority(Authority.ROLE_ADMIN);
//
//        User findUser = userService.findUser(user.getUserId()).get();
//
//        testHelper.assertUser(findUser, "user1", Authority.ROLE_USER, Authority.ROLE_ADMIN);
    }

    @Test
    @DisplayName("4. authority를 제거한다.")
    void test_4() {

    }

    @Test
    @DisplayName("5. email 로 검색이 된다.")
    void test_5() {
        User user1 = testHelper.createUser("user1");

        User findUser = (User) userService.loadUserByUsername("user1@test.com");

        testHelper.assertUser(findUser, "user1");
    }

    @Test
    @DisplayName("6. role 이 중복돼서 추가되지 않는다.")
    void test_6() {

    }

}