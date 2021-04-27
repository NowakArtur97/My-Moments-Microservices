package com.nowakArtur97.myMoments.postService.configuration.security;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;

@Configuration
@EnableWebFluxSecurity
@EnableConfigurationProperties(value = JwtConfigurationProperties.class)
@RequiredArgsConstructor
class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final JwtConfigurationProperties jwtConfigurationProperties;

    private final CustomReactiveAuthenticationManager customReactiveAuthenticationManager;

    private final CustomServerSecurityContextRepository customServerSecurityContextRepository;

    @Bean
    PasswordEncoder getBCryptPasswordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationProvider getDaoAuthenticationProvider() {

        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();

        daoAuthenticationProvider.setPasswordEncoder(getBCryptPasswordEncoder());

        return daoAuthenticationProvider;
    }

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
