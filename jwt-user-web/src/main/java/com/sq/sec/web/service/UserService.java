package com.sq.sec.web.service;

import com.sq.sec.web.domain.Authority;
import com.sq.sec.web.domain.User;
import com.sq.sec.web.repository.AuthorityRepository;
import com.sq.sec.web.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;

    public User save(User user, String role) {

        Authority authority = Authority.builder()
                .authority(role)
                .email(user.getEmail())
                .build();

        authorityRepository.save(authority);

        return userRepository.save(user);
    }

    public Optional<User> findUser(Long id) {
        return userRepository.findById(id);
    }

}
