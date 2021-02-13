package com.sq.sec.web.controller;

import com.sq.sec.web.domain.Authority;
import com.sq.sec.web.domain.User;
import com.sq.sec.web.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    
    @PostMapping("/save")
    public User saveUser(@RequestBody User user) {

        Authority authority = Authority.builder()
                .email(user.getEmail())
                .authority("ROLE_USER")
                .build();

        return userService.save(user, authority);
    }

    @GetMapping("/{userId}")
    public Optional<User> getUser(@PathVariable Long userId) {
        return userService.findUser(userId);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @GetMapping("/list")
    public List<User> userList() {
        return userService.findAll();
    }

}
