package com.sq.sec.web.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sq.sec.web.security.CustomUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomUserService customUserService;
    private final ObjectMapper objectMapper;
    private final JWTUtil jwtUtil;

    public SecurityConfig(CustomUserService customUserService, ObjectMapper objectMapper, JWTUtil jwtUtil) {
        this.customUserService = customUserService;
        this.objectMapper = objectMapper;
        this.jwtUtil = jwtUtil;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserService)
                .passwordEncoder(passwordEncoder());
    }

    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        RefreshableLoginFilter jwtLoginFilter = new RefreshableLoginFilter(authenticationManager(), customUserService, jwtUtil, objectMapper);
        JWTCheckFilter jwtCheckFilter = new JWTCheckFilter(authenticationManager(), customUserService, jwtUtil);

        http
                .csrf().disable()
                .addFilter(jwtLoginFilter)
                .addFilter(jwtCheckFilter)
        ;
    }
}











