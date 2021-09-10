package com.nowakArtur97.myMoments.followerService.feature.resource;

import com.nowakArtur97.myMoments.followerService.advice.ErrorResponse;
import com.nowakArtur97.myMoments.followerService.exception.ForbiddenException;
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
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
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

    @Test
    void when_follow_user_for_a_second_time_should_throw_exception() {

        String header = "Bearer token";
        String username = "user";
        String usernameToFollow = "usernameToFollow";

        when(jwtUtil.extractUsernameFromHeader(header)).thenReturn(username);
        String exceptionMessage = "User is already following: " + usernameToFollow + ".";
        when(followerService.followUser(username, usernameToFollow)).thenThrow(new ForbiddenException(exceptionMessage));

        Mono<ErrorResponse> errorResponseMono = webTestClient.post()
                .uri(FOLLOW_BASE_PATH, usernameToFollow)
                .header("Authorization", header)
                .exchange()
                .expectStatus()
                .isForbidden()
                .returnResult(ErrorResponse.class)
                .getResponseBody()
                .single();

        StepVerifier.create(errorResponseMono)
                .thenConsumeWhile(
                        errorResponse -> {
                            assertAll(() -> assertEquals(exceptionMessage,
                                    errorResponse.getErrors().get(0),
                                    () -> "should return error response with message: " + exceptionMessage + ", but was: "
                                            + errorResponse.getErrors().get(0)),
                                    () -> assertEquals(1, errorResponse.getErrors().size(),
                                            () -> "should return error response with 1 message, but was: "
                                                    + errorResponse.getErrors().size()),
                                    () -> assertNotNull(errorResponse.getTimestamp(),
                                            () -> "should return error response with not null timestamp, but was: null"),
                                    () -> assertEquals(HttpStatus.FORBIDDEN.value(), errorResponse.getStatus(),
                                            () -> "should return error response with " + HttpStatus.FORBIDDEN.value() + " status, but was: "
                                                    + errorResponse.getStatus()),
                                    () -> verify(jwtUtil, times(1)).extractUsernameFromHeader(header),
                                    () -> verifyNoMoreInteractions(jwtUtil),
                                    () -> verify(followerService, times(1)).followUser(username, usernameToFollow),
                                    () -> verifyNoMoreInteractions(followerService));
                            return true;
                        }
                ).verifyComplete();
    }

    @Test
    void when_follow_user_without_username_should_return_error_response() {

        String header = "Bearer token";
        String invalidUsernameToFollow = " ";

        Mono<ErrorResponse> errorResponseMono = webTestClient.post()
                .uri(FOLLOW_BASE_PATH, invalidUsernameToFollow)
                .header("Authorization", header)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .returnResult(ErrorResponse.class)
                .getResponseBody()
                .single();

        StepVerifier.create(errorResponseMono)
                .thenConsumeWhile(
                        errorResponse -> {
                            assertAll(
                                    () -> assertEquals("Username cannot be empty.",
                                            errorResponse.getErrors().get(0),
                                            () -> "should return error response with message: " +
                                                    "'Username cannot be empty.'" + ", but was: "
                                                    + errorResponse.getErrors().get(0)),
                                    () -> assertEquals(1, errorResponse.getErrors().size(),
                                            () -> "should return error response with 1 message, but was: "
                                                    + errorResponse.getErrors().size()),
                                    () -> assertNotNull(errorResponse.getTimestamp(),
                                            () -> "should return error response with not null timestamp, but was: null"),
                                    () -> assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus(),
                                            () -> "should return error response with " + HttpStatus.BAD_REQUEST.value() + " status, but was: "
                                                    + errorResponse.getStatus()),
                                    () -> verifyNoInteractions(jwtUtil),
                                    () -> verifyNoInteractions(followerService));
                            return true;
                        }
                ).verifyComplete();
    }
}
