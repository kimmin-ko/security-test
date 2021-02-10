package com.sq.sec.web.config;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserLogin {
    private String username;
    private String password;
}
