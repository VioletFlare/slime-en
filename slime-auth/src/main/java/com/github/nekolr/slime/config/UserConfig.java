package com.github.nekolr.slime.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Slf4j
public class UserConfig {

    /**
     * User Name
     */
    @Value("${spider.username}")
    private String username;

    /**
     * Password
     */
    @Value("${spider.password}")
    private String password;

}
