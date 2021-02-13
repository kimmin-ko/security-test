package com.sq.sec.web.config;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserLogin {

    public enum Type {
        login,
        refresh
    }

    private Type type;
    private String username;
    private String password;
    private String refreshToken;
}
