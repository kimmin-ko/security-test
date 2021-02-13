package com.sq.sec.web;

import com.sq.sec.web.config.SpJwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@EnableConfigurationProperties({SpJwtProperties.class})
@SpringBootApplication
public class JwtUserWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(JwtUserWebApplication.class, args);
    }

}
