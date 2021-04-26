package com.nowakArtur97.myMoments.userService.configuration.security;

import com.nowakArtur97.myMoments.userService.common.util.JwtUtil;
import com.nowakArtur97.myMoments.userService.exception.JwtTokenMissingException;
import com.nowakArtur97.myMoments.userService.feature.document.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(value = JwtConfigurationProperties.class)
class JwtRequestFilter extends OncePerRequestFilter {

    private final CustomUserDetailsService customUserDetailsService;

    private final JwtUtil jwtUtil;

    private final JwtConfigurationProperties jwtConfigurationProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader = request.getHeader(jwtConfigurationProperties.getAuthorizationHeader());

        String username;
        String jwt;

        if (isBearerTypeAuthorization(authorizationHeader)) {

            jwt = authorizationHeader.substring(jwtConfigurationProperties.getAuthorizationHeaderLength());
            username = jwtUtil.extractUsername(jwt);
        } else {
            throw new JwtTokenMissingException("JWT token is missing in request headers.");
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null && username != null) {

            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

            if (jwtUtil.isTokenValid(jwt, userDetails)) {

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                        = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String path = request.getRequestURI().substring(request.getContextPath().length());

        return jwtConfigurationProperties.getIgnoredEndpoints().stream().anyMatch(path::contains);
    }

    private boolean isBearerTypeAuthorization(String authorizationHeader) {

        return authorizationHeader != null && authorizationHeader.startsWith(jwtConfigurationProperties.getAuthorizationType());
    }
}
