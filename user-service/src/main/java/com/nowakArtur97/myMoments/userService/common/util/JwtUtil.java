package com.nowakArtur97.myMoments.userService.common.util;


import com.nowakArtur97.myMoments.userService.configuration.security.JwtConfigurationProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(value = JwtConfigurationProperties.class)
@Slf4j
public class JwtUtil {

    private final JwtConfigurationProperties jwtConfigurationProperties;

    public String generateToken(UserDetails userDetails) {

        log.info(jwtConfigurationProperties.getSecretKey());

        Map<String, Object> claims = new HashMap<>();

        return createToken(userDetails.getUsername(), claims);
    }

    public String extractUsernameFromHeader(String authorizationHeader) {

        String token = authorizationHeader.substring(jwtConfigurationProperties.getAuthorizationHeaderLength());

        return extractClaim(token, Claims::getSubject);
    }

    public String extractUsername(String token) {

        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {

        Claims claim = extractAllClaims(token);

        return claimsResolver.apply(claim);
    }

    private Claims extractAllClaims(String token) {

        return Jwts.parser().setSigningKey(jwtConfigurationProperties.getSecretKey()).parseClaimsJws(token).getBody();
    }

    private String createToken(String subject, Map<String, Object> claims) {

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtConfigurationProperties.getValidity()))
                .signWith(SignatureAlgorithm.HS256, jwtConfigurationProperties.getSecretKey())
                .compact();
    }

    public String getJwtFromHeader(String authorizationHeader) {

        return authorizationHeader != null
                ? authorizationHeader.substring(jwtConfigurationProperties.getAuthorizationHeaderLength())
                : "";
    }

    public String getAuthorizationHeader(HttpServletRequest request) {

        return request.getHeader(jwtConfigurationProperties.getAuthorizationHeader());
    }

    public boolean isNotSecured(String path) {
        return jwtConfigurationProperties.getIgnoredEndpoints().stream().anyMatch(path::contains);
    }
}
