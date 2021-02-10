package com.sp.sec.web.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.Authentication;

@Data
@Builder
public class SecurityMessage {

    private String message;

    @JsonIgnore
    private Authentication auth;
}
