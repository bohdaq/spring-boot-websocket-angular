package com.example.springbootwebsocketangularv002.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenUtil {
    private String secretKey = "QFB12nWdiCQ6uN/txgvF+Ta8khIc7HpWo8tBjHrbXm657Vaxnk8hHKQAG0ikcO4IC5wUcBKZ21+bcadi6AqrC7Epp9LfzkQ6McnZSXQ5rkwPIIcpiAyC8ZvbgSJ/No7lGLG2NLx3b413Kf8YeL04BOm7yHjLnkmHvDis6uxUPafo";

    private static final long EXPIRATION_TIME = 86400000;

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    public Claims getClaimsFromToken(String token) {
        JwtParser jwtParser = Jwts.parser().setSigningKey(secretKey).build();
        return jwtParser.parseClaimsJws(token).getBody();
    }

    public boolean validateToken(String token, String username) {
        String tokenUsername = getUsernameFromToken(token);
        return (tokenUsername.equals(username) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return getClaimsFromToken(token).getExpiration().before(new Date());
    }
}
