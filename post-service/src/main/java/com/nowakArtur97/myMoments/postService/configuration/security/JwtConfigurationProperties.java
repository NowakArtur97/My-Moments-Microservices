package com.nowakArtur97.myMoments.postService.configuration.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.util.List;

@ConfigurationProperties(prefix = "my-moments.jwt")
@ConstructorBinding
@AllArgsConstructor
@Getter
public final class JwtConfigurationProperties {

    private final String secretKey;

    private final long validity;

    private final List<String> ignoredEndpoints;

    private final List<String> ignoredAntMatchers;

    private final List<String> authenticatedAntMatchers;

    private final String authorizationHeader;

    private final String authorizationType;

    private final int authorizationHeaderLength;
}
