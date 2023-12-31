package com.github.nekolr.slime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

// 取消 UserDetailsServiceAutoConfiguration Auto-configure
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class, scanBasePackages = "com.github.nekolr")
@EnableScheduling
public class SlimeApplication {

    public static void main(String[] args) {
        SpringApplication.run(SlimeApplication.class, args);
    }

}
