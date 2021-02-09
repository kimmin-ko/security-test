package com.sp.sec.user.service;

import com.sp.sec.user.domain.Authority;
import com.sp.sec.user.domain.User;
import com.sp.sec.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.springframework.data.mongodb.core.query.Criteria.*;
import static org.springframework.data.mongodb.core.query.Query.*;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final MongoTemplate mongoTemplate;
    private final UserRepository userRepository;

    public User save(User user) {
        if (StringUtils.isEmpty(user.getUserId()))
            user.setCreated(LocalDateTime.now());

        user.setUpdated(LocalDateTime.now());
//        user.setEnabled(true);

        return userRepository.save(user);
    }

    public Optional<User> findUser(String userId) {
        return userRepository.findById(userId);
    }

    public boolean updateUserName(String userId, String userName) {
        Update update = new Update();
        update.set("name", userName);
        update.set("updated", LocalDateTime.now());

        return mongoTemplate.updateFirst(query(where("userId").is(userId)),
                update, User.class).wasAcknowledged();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + "이 존재하지 않음."));
    }

    public boolean addAuthority(String userId, String authority) {
        Update update = new Update();
        update.push("authorities", new Authority(authority));
        update.set("updated", LocalDateTime.now());

        return mongoTemplate.updateFirst(query(where(userId).is(userId)), update, User.class)
                .wasAcknowledged();
    }

    public boolean removeAuthority(String userId, String authority) {
        Update update = new Update();
        update.pull("authorities", new Authority(authority));
        update.set("updated", LocalDateTime.now());

        return mongoTemplate.updateFirst(query(where(userId).is(userId)),
                update, User.class).wasAcknowledged();
    }

}
