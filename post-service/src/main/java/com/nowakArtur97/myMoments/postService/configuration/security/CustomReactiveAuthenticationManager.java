package com.nowakArtur97.myMoments.postService.configuration.security;

import com.nowakArtur97.myMoments.postService.common.util.JwtUtil;
import com.nowakArtur97.myMoments.postService.feature.user.document.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
class CustomReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtUtil jwtUtil;

    private final UserRepository userRepository;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {

        String usernameOrEmail = authentication.getCredentials().toString();

        return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .map(userDocument -> new User(userDocument.getUsername(), userDocument.getPassword(),
                        userDocument.getRoles().stream()
                                .map(roleDocument -> new SimpleGrantedAuthority(roleDocument.getName()))
                                .collect(Collectors.toList())))
                .flatMap(userDetails -> {

                    String jwt = authentication.getPrincipal().toString();

                    if (jwtUtil.isTokenValid(jwt, userDetails)) {

                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                                = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

                        return Mono.just(usernamePasswordAuthenticationToken);

                    } else {
                        return Mono.empty();
                    }
                });
    }
}
