package com.sq.sec.web.security;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

@Data
public class CustomAuthority implements GrantedAuthority {

    private String authority;

}