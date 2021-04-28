package com.nowakArtur97.myMoments.postService.configuration.security;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true)
@EnableConfigurationProperties(value = JwtConfigurationProperties.class)
@RequiredArgsConstructor
class WebSecurityConfiguration {

    private final CustomReactiveAuthenticationManager customReactiveAuthenticationManager;

    private final CustomServerSecurityContextRepository customServerSecurityContextRepository;

    private final JwtConfigurationProperties jwtConfigurationProperties;

    @Bean
    SecurityWebFilterChain getSecurityWebFilterChain(ServerHttpSecurity httpSecurity) {

        return httpSecurity
                .authorizeExchange()
                .pathMatchers(jwtConfigurationProperties.getIgnoredAntMatchers().toArray(new String[0]))
                .permitAll()
                .pathMatchers(jwtConfigurationProperties.getAuthenticatedAntMatchers().toArray(new String[0]))
                .authenticated()
                .and()
                .httpBasic()
                .disable()
                .csrf()
                .disable()
                .formLogin()
                .disable()
                .logout()
                .disable()
                .authenticationManager(customReactiveAuthenticationManager)
                .securityContextRepository(customServerSecurityContextRepository)
                .build();
    }
}
