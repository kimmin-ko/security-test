package com.sq.sec.web.service;

import com.sq.sec.web.domain.Authority;
import com.sq.sec.web.domain.User;
import com.sq.sec.web.repository.AuthorityRepository;
import com.sq.sec.web.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;

    public User save(User user, Authority authority) {
        authorityRepository.save(authority);

        return userRepository.save(user);
    }

    public Optional<User> findUser(Long id) {
        return userRepository.findById(id);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void clearUsers() {
        userRepository.deleteAll();
        authorityRepository.deleteAll();
    }

    public void addAuthority(Long id, String role) {
        User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException(id + "번 회원이 존재하지 않습니다."));

        Authority authority = Authority.builder()
                .email(user.getEmail())
                .authority(role)
                .build();

        authorityRepository.save(authority);
    }
}
