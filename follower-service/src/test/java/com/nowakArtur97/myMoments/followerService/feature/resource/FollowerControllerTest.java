package com.nowakArtur97.myMoments.followerService.feature.resource;

import com.nowakArtur97.myMoments.followerService.feature.node.FollowerService;
import com.nowakArtur97.myMoments.followerService.jwt.JwtUtil;
import com.nowakArtur97.myMoments.followerService.testUtil.generator.NameWithSpacesGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("FollowerController_Tests")
class FollowerControllerTest {

    @LocalServerPort
    private int serverPort;

    private String FOLLOWERS_BASE_PATH;
    private String FOLLOW_BASE_PATH;

    @MockBean
    private FollowerService followerService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {

        webTestClient = webTestClient
                .mutate()
                .responseTimeout(Duration.ofMillis(30000))
                .build();

        FOLLOWERS_BASE_PATH = "http://localhost:" + serverPort + "/api/v1/followers";
        FOLLOW_BASE_PATH = FOLLOWERS_BASE_PATH + "/{username}";
    }

    @Test
    void when_follow_user_should_not_return_content() {

        String header = "Bearer token";
        String username = "user";
        String usernameToFollow = "usernameToFollow";

        Void mock = mock(Void.class);
        when(jwtUtil.extractUsernameFromHeader(header)).thenReturn(username);
        when(followerService.followUser(username, usernameToFollow)).thenReturn(Mono.just(mock));

        webTestClient.post()
                .uri(FOLLOW_BASE_PATH, usernameToFollow)
                .header("Authorization", header)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .isEmpty();

        assertAll(
                () -> verify(jwtUtil, times(1)).extractUsernameFromHeader(header),
                () -> verifyNoMoreInteractions(jwtUtil),
                () -> verify(followerService, times(1)).followUser(username, usernameToFollow),
                () -> verifyNoMoreInteractions(followerService));
    }
}
