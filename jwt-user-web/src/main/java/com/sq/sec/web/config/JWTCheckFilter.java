package com.sq.sec.web.config;

import com.sq.sec.web.domain.User;
import com.sq.sec.web.security.CustomUser;
import com.sq.sec.web.security.CustomUserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;

public class JWTCheckFilter extends BasicAuthenticationFilter {

    private final CustomUserService customUserService;
    private final JWTUtil jwtUtil;

    public JWTCheckFilter(AuthenticationManager authenticationManager, CustomUserService customUserService, JWTUtil jwtUtil) {
        super(authenticationManager);
        this.customUserService = customUserService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {
        String token = request.getHeader(JWTUtil.AUTH_HEADER);
        if (Objects.isNull(token) || !token.startsWith(JWTUtil.BEARER)) {
            chain.doFilter(request, response);
            return;
        }

        VerifyResult result = jwtUtil.verify(token.substring(JWTUtil.BEARER.length()));
        if(result.isResult()) {
            CustomUser user = customUserService.findUser(result.getUserId());
            Set<GrantedAuthority> authorities = customUserService.getAuthorities(user.getEmail());
            Authentication auth = new UsernamePasswordAuthenticationToken(user, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        chain.doFilter(request, response);
    }
}
