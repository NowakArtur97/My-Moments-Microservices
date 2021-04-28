package com.nowakArtur97.myMoments.postService.configuration.security;

import com.nowakArtur97.myMoments.postService.common.util.JwtUtil;
import com.nowakArtur97.myMoments.postService.exception.JwtTokenMissingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
class CustomServerSecurityContextRepository implements ServerSecurityContextRepository {

    private final JwtUtil jwtUtil;

    private final JwtConfigurationProperties jwtConfigurationProperties;

    private final CustomReactiveAuthenticationManager customReactiveAuthenticationManager;

    @Override
    public Mono<Void> save(ServerWebExchange serverWebExchange, SecurityContext securityContext) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange serverWebExchange) {

        ServerHttpRequest request = serverWebExchange.getRequest();
        String authorizationHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        String username;
        String jwt;

        if (isBearerTypeAuthorization(authorizationHeader)) {

            jwt = authorizationHeader.substring(jwtConfigurationProperties.getAuthorizationHeaderLength());
            username = jwtUtil.extractUsername(jwt);

        } else {
            throw new JwtTokenMissingException("JWT token is missing in request headers.");
        }

        if (username != null) {

            Authentication auth = new UsernamePasswordAuthenticationToken(jwt, username);

            return customReactiveAuthenticationManager.authenticate(auth).map(SecurityContextImpl::new);
        }

        return Mono.empty();
    }

    private boolean isBearerTypeAuthorization(String authorizationHeader) {

        return authorizationHeader != null && authorizationHeader.startsWith(jwtConfigurationProperties.getAuthorizationType());
    }
}
