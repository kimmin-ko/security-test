package com.sq.sec.web.security;

import com.sq.sec.web.domain.Authority;
import com.sq.sec.web.domain.User;
import com.sq.sec.web.repository.AuthorityRepository;
import com.sq.sec.web.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CustomUserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + "을 찾을 수 없습니다."));

        return CustomUser.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .authorities(getAuthorities(user.getEmail()))
                .enabled(true)
                .build();
    }

    public Set<GrantedAuthority> getAuthorities(String username) {
        List<Authority> authorities = authorityRepository.findByEmail(username);
        Set<GrantedAuthority> results = new HashSet<>();

        authorities.forEach(authority -> results.add(new SimpleGrantedAuthority(authority.getAuthority())));

        return results;
    }

}