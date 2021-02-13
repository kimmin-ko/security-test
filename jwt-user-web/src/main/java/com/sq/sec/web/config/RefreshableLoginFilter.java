package com.sq.sec.web.config;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sq.sec.web.domain.User;
import com.sq.sec.web.security.CustomUser;
import com.sq.sec.web.security.CustomUserService;
import lombok.SneakyThrows;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RefreshableLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final CustomUserService customUserService;
    private final JWTUtil jwtUtil;
    private final ObjectMapper objectMapper;

    public RefreshableLoginFilter(AuthenticationManager authenticationManager, CustomUserService customUserService, JWTUtil jwtUtil, ObjectMapper objectMapper) {
        this.authenticationManager = authenticationManager;
        this.customUserService = customUserService;
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
        // (POST) /login 으로 들어온 Request에 대해서 Filter 처리
        setFilterProcessesUrl("/login");
    }

    // login 요청이 들어오면 호출됨 (SecurityConfig에 등록 필요)
    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        UserLogin userLogin = objectMapper.readValue(request.getInputStream(), UserLogin.class);

        if (userLogin.getType().equals(UserLogin.Type.login)) {
            // id password login
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userLogin.getUsername(), userLogin.getPassword(), null);

            return authenticationManager.authenticate(authToken);
        } else if (userLogin.getType().equals(UserLogin.Type.refresh)) {
            // refresh token login
            if (StringUtils.isEmpty(userLogin.getRefreshToken()))
                throw new IllegalArgumentException("리프레쉬 토큰이 필요함. : " + userLogin.getRefreshToken());

            VerifyResult result = jwtUtil.verify(userLogin.getRefreshToken());
            if (result.isResult()) {
                CustomUser user = customUserService.findUser(result.getUserId());
                return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            } else {
                throw new TokenExpiredException("리프레쉬 토큰이 만료됨");
            }

        } else {
            throw new IllegalArgumentException("알 수 없는 타입: " + userLogin.getType());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        CustomUser user = (CustomUser) authResult.getPrincipal();
        response.setHeader(JWTUtil.AUTH_HEADER, JWTUtil.BEARER + jwtUtil.generate(user.getUserId()));
        response.setHeader(JWTUtil.REFRESH_HEADER, jwtUtil.generate(user.getUserId()));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        System.out.println("failed = " + failed.getMessage());
        super.unsuccessfulAuthentication(request, response, failed);
    }

}