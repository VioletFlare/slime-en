package com.github.nekolr.slime.security.filter;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;

@Slf4j
@Component
public class TokenProvider {

    private final Duration period;
    private final SecretKey secretKey;

    public TokenProvider(@Value("${jwt.period}") Duration period) {
        this.period = period;
        this.secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }

    public String createToken(String username) {
        long now = new Date().getTime();
        Date expireDate = new Date(now + period.toMillis());

        return Jwts.builder()
                .setSubject(username)
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .setExpiration(expireDate)
                .compact();
    }

    public String getUsername(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            // token Overdue
            log.error("Token has expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            // token Text to translate: Format error
            log.error("Token format error: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            // token Construct Error
            log.error("Token construct error: {}", e.getMessage());
        } catch (SignatureException e) {
            // Signature Failed
            log.error("Signature failed: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            // Illegal Parameters
            log.error("Illegal argument: {}", e.getMessage());
        } catch (JwtException e) {
            // Other abnormalities
            log.error("Other exception: {}", e.getMessage());
        }
        return false;
    }
}
