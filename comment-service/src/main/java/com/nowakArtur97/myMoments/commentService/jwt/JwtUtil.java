package com.nowakArtur97.myMoments.commentService.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(value = JwtConfigurationProperties.class)
public class JwtUtil {

    private final JwtConfigurationProperties jwtConfigurationProperties;

    public String extractUsernameFromHeader(String authorizationHeader) {

        String token = authorizationHeader.substring(jwtConfigurationProperties.getAuthorizationHeaderLength());

        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {

        Claims claim = extractAllClaims(token);

        return claimsResolver.apply(claim);
    }

    private Claims extractAllClaims(String token) {

        return Jwts.parser().setSigningKey(jwtConfigurationProperties.getSecretKey()).parseClaimsJws(token).getBody();
    }
}
