package com.sq.sec.web;

import com.sq.sec.web.domain.User;
import com.sq.sec.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

public class SpJwtRefreshableTwoUserIntegrationTest extends SpRefreshableIntegrationTest {

    @Autowired
    protected UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    protected UserTestHelper userTestHelper;

    protected User USER1;
    protected User USER2;

    protected void prepareTwoUsers(){
        userService.clearUsers();
        this.userTestHelper = new UserTestHelper(userService, passwordEncoder);
        this.USER1 = this.userTestHelper.createUser("user1", "ROLE_USER");
        this.USER2 = this.userTestHelper.createUser("user2", "ROLE_USER");
    }

}
