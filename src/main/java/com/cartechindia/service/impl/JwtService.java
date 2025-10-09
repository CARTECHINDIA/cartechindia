package com.cartechindia.service.impl;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    private static final String SECRET = "change_this_very_long_random_secret_key_change_me";
    private static final Key KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    private static final String ROLES_CLAIM = "roles";

    public String generateToken(String subjectEmail, Collection<? extends GrantedAuthority> authorities) {
        Instant now = Instant.now();
        List<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority) // e.g. ROLE_ADMIN
                .toList();

        return Jwts.builder()
                .setSubject(subjectEmail)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(2, ChronoUnit.HOURS)))
                .claim(ROLES_CLAIM, roles)
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsername(String token) {
        return parse(token).getBody().getSubject();
    }

    public List<String> getRoles(String token) {
        Claims claims = parse(token).getBody();
        Object raw = claims.get(ROLES_CLAIM);
        if (raw instanceof Collection<?> c) {
            return c.stream().map(String::valueOf).toList();
        }
        return List.of();
    }

    public boolean isValid(String token) {
        try {
            Claims claims = parse(token).getBody();
            return claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Jws<Claims> parse(String token) {
        return Jwts.parserBuilder().setSigningKey(KEY).build().parseClaimsJws(token);
    }
}
