package com.github.nekolr.slime.config;

import com.github.nekolr.slime.security.filter.JwtAuthenticationEntryPoint;
import com.github.nekolr.slime.security.filter.JwtAuthenticationFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity

                // Close Other
                .logout().disable()
                // Off csrf
                .csrf().disable()

                // X-Frame-Options: SAMEORIGIN
                .headers()
                .frameOptions()
                .sameOrigin()

                // X-Content-Type-Options: nosniff
                .and()
                .headers()
                .contentTypeOptions()

                // X-XSS-Protection: 1; mode=block
                .and().and()
                .headers()
                .xssProtection()
                .xssProtectionEnabled(true)

                // Grant access to perform unusual actions
                .and().and()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)

                // 不需要 session
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                // The following text is not translated:
                .and()
                .authorizeRequests()
                // OPTIONS The pre-flight check can be accessed anonymously
                .antMatchers(HttpMethod.OPTIONS, "/**").anonymous()
                // Public resources can be accessed anonymously
                .antMatchers(HttpMethod.GET, "/js/**").anonymous()
                .antMatchers(HttpMethod.GET, "/css/**").anonymous()
                .antMatchers(HttpMethod.GET, "/images/**").anonymous()
                .antMatchers(HttpMethod.GET, "/*.html").anonymous()
                // Home page is accessible without registration
                .antMatchers(HttpMethod.GET, "/").anonymous()
                // Log in request pending（If the login request contains Authorization: Bearer Any Character，Then we will proceed to the verification.）
                .antMatchers(HttpMethod.POST, "/auth/login").permitAll()
                // Allow websocket Request
                .antMatchers("/ws").permitAll()

                // Other Requests Will Require Authentication
                .anyRequest().authenticated();

        httpSecurity
                // Add two filters for adding logins and permission checks
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
