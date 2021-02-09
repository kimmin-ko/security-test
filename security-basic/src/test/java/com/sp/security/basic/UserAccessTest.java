package com.sp.security.basic;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest
public class UserAccessTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    UserDetails user1() {
        return User.builder()
                .username("user1")
                .password(passwordEncoder.encode("1234"))
                .roles("USER")
                .build();
    }


    UserDetails admin() {
        return User.builder()
                .username("admin")
                .password(passwordEncoder.encode("1234"))
                .roles("ADMIN")
                .build();
    }


    @Test
    @DisplayName("1. user 로 user 페이지를 접근할 수 있다.")
    void test_user_access_userpage() throws Exception {
        String resp = mockMvc.perform(get("/user").with(user(user1())))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        SecurityMessage message = objectMapper.readValue(resp, SecurityMessage.class);

        assertThat(message.getMessage()).isEqualTo("user page");
    }

    @Test
    @DisplayName("2. user 로 admin 페이지를 접근할 수 없다.")
    void test_user_access_adminpage() throws Exception {
        mockMvc.perform(get("/admin").with(user(user1())))
                .andExpect(status().isForbidden())
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    @DisplayName("3. admin 이 user 페이지와 admin 페이지를 접근할 수 있다.")
    void test_admin_access_userpage_and_adminpage() throws Exception {
        String respUser = mockMvc.perform(get("/user").with(user(admin())))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        SecurityMessage userMessage = objectMapper.readValue(respUser, SecurityMessage.class);

        assertThat(userMessage.getMessage()).isEqualTo("user page");

        String respAdmin = mockMvc.perform(get("/admin").with(user(admin())))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        SecurityMessage adminMessage = objectMapper.readValue(respAdmin, SecurityMessage.class);

        assertThat(adminMessage.getMessage()).isEqualTo("admin page");
    }

    @Test
    @DisplayName("4. login 페이지는 아무나 접근할 수 있어야 한다.")
    void test_login_page_can_accessed_anonymous() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("5. / 홈페이지는 로그인 하지 않은 사람은 접근할 수 없다.")
    void test_() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection());
    }

}
