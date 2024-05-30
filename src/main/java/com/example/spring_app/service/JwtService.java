package com.example.spring_app.service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.internal.Function;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.example.spring_app.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtService {

    @Value("${security.jwt.secret-key}")
    private String SECRECT_KEY;

    @Value("${security.jwt.expire-min}")
    private long EXPIRE;

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String generateToken(
            User user) {
        return generateToken(new HashMap<>(), user);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUserName(token);
        return username.equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpriration(token).before(new Date());
    }

    private Date extractExpriration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    @SuppressWarnings("deprecation")
    public String generateToken(
            Map<String, Object> claims,
            User user) {
        Date issueAt = new Date(System.currentTimeMillis());
        Date expiration = new Date(issueAt.getTime() + (EXPIRE * 60 * 60 * 1000));
        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(issueAt)
                .setExpiration(expiration)
                .signWith(getSigningKey(), io.jsonwebtoken.SignatureAlgorithm.HS256)
                .compact();
    }

    // extract single claim
    public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    // extract all claims in jwt
    @SuppressWarnings("deprecation")
    public Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRECT_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
