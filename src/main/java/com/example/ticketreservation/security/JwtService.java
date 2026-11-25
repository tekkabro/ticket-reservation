package com.example.ticketreservation.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Service
public class JwtService {
    private final Key key;
    private final long expirationMs;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.expiration-ms}") long expirationMs) {
        // 48-byte secret -> HS384 is chosen automatically
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public long getExpirationMs() { return expirationMs; }

    public String generateToken(UserDetails user) {
        Date now = new Date();
        String role = user.getAuthorities().stream().findFirst().map(GrantedAuthority::getAuthority).orElse("");
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expirationMs))
                .signWith(key)
                .compact();
    }

    private Claims parse(String token) {
        // throws JwtException if the signature is wrong or the token is expired
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public String extractUsername(String token) { return parse(token).getSubject(); }

    public boolean isTokenValid(String token, UserDetails user) {
        Claims c = parse(token);
        return c.getSubject().equals(user.getUsername()) && c.getExpiration().after(new Date());
    }
}
