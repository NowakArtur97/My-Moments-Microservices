package com.nowakArtur97.myMoments.gatewayService.gateway;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(value = JwtConfigurationProperties.class)
class JwtUtil {

    private final JwtConfigurationProperties jwtConfigurationProperties;

    boolean isTokenValid(String token, String username) {

        return (extractUsername(token).equals(username) && !isTokenExpired(token));
    }

    String extractUsername(String token) {

        String subject = extractClaim(token, Claims::getSubject);

        return subject != null ? subject : "";
    }

    Date extractExpirationDate(String token) {

        return extractClaim(token, Claims::getExpiration);
    }

    <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {

        Claims claim = extractAllClaims(token);

        return claimsResolver.apply(claim);
    }

    private Claims extractAllClaims(String token) {

        return Jwts.parser().setSigningKey(jwtConfigurationProperties.getSecretKey()).parseClaimsJws(token).getBody();
    }

    private boolean isTokenExpired(String token) {

        return extractExpirationDate(token).before(new Date(System.currentTimeMillis()));
    }

    boolean isBearerTypeAuthorization(String authorizationHeader) {

        return authorizationHeader != null && authorizationHeader.startsWith(jwtConfigurationProperties.getAuthorizationType());
    }

    String getJwtFromHeader(String authorizationHeader) {

        return authorizationHeader != null
                ? authorizationHeader.substring(jwtConfigurationProperties.getAuthorizationHeaderLength())
                : "";
    }

    String getAuthorizationHeader(ServerHttpRequest serverHttpRequest) {

        return serverHttpRequest.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    }

    boolean isNotSecured(String path) {
        return jwtConfigurationProperties.getIgnoredEndpoints().stream().anyMatch(path::contains);
    }
}
