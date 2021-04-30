package com.nowakArtur97.myMoments.postService.common.util;

import com.nowakArtur97.myMoments.postService.configuration.security.JwtConfigurationProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(value = JwtConfigurationProperties.class)
public class JwtUtil {

    private final JwtConfigurationProperties jwtConfigurationProperties;

    public boolean isTokenValid(String token, UserDetails userDetails) {

        return (extractUsername(token).equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String extractUsernameFromHeader(String authorizationHeader) {

        String token = authorizationHeader.substring(jwtConfigurationProperties.getAuthorizationHeaderLength());

        return extractClaim(token, Claims::getSubject);
    }

    public String extractUsername(String token) {

        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpirationDate(String token) {

        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {

        Claims claim = extractAllClaims(token);

        return claimsResolver.apply(claim);
    }

    private Claims extractAllClaims(String token) {

        return Jwts.parser().setSigningKey(jwtConfigurationProperties.getSecretKey()).parseClaimsJws(token).getBody();
    }

    private boolean isTokenExpired(String token) {

        return extractExpirationDate(token).before(new Date(System.currentTimeMillis()));
    }

    public boolean isBearerTypeAuthorization(String authorizationHeader) {

        return authorizationHeader != null && authorizationHeader.startsWith(jwtConfigurationProperties.getAuthorizationType());
    }

    public String getJwtFromHeader(String authorizationHeader) {

        return authorizationHeader != null
                ? authorizationHeader.substring(jwtConfigurationProperties.getAuthorizationHeaderLength())
                : "";
    }
}