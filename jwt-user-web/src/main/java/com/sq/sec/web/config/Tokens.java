package com.sq.sec.web.config;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Tokens {
    private String accessToken;
    private String refreshToken;
}
