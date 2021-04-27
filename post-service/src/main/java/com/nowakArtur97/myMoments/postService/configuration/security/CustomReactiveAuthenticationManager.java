package com.nowakArtur97.myMoments.postService.configuration.security;

import com.nowakArtur97.myMoments.postService.common.util.JwtUtil;
import com.nowakArtur97.myMoments.postService.feature.user.document.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
class CustomReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtUtil jwtUtil;

    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {

        String jwt = authentication.getCredentials().toString();

        String username = jwtUtil.extractUsername(jwt);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        if (jwtUtil.isTokenValid(jwt, userDetails)) {

            return Mono.just(new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));
        }

        return Mono.empty();
    }
}
