package com.sp.sec.user.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "sp_user")
public class User implements UserDetails {

    @Id
    private String userId;

    @Indexed(unique = true)
    private String email;
    private String name;
    private String password;

    private boolean enabled;

    private Set<Authority> authorities = new HashSet<>();

    private LocalDateTime created;
    private LocalDateTime updated;

    public void addAuthority(String authority) {
        authorities.add(new Authority(authority));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return enabled;
    }

    @Override
    public boolean isAccountNonLocked() {
        return enabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return enabled;
    }

}
