package com.nowakArtur97.myMoments.postService.feature.resource;

import com.nowakArtur97.myMoments.postService.advice.ErrorResponse;
import com.nowakArtur97.myMoments.postService.exception.ForbiddenException;
import com.nowakArtur97.myMoments.postService.exception.ResourceNotFoundException;
import com.nowakArtur97.myMoments.postService.feature.document.PostService;
import com.nowakArtur97.myMoments.postService.jwt.JwtUtil;
import com.nowakArtur97.myMoments.postService.testUtil.generator.NameWithSpacesGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("PostController_Tests")
class PostDeleteControllerTest {

    @LocalServerPort
    private int serverPort;

    private String POSTS_BASE_PATH;

    @MockBean
    private PostService postService;

    @MockBean
    private PostObjectMapper postObjectMapper;

    @MockBean
    private ModelMapper modelMapper;

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

        POSTS_BASE_PATH = "http://localhost:" + serverPort + "/api/v1/posts/{id}";
    }

    @Test
    void when_delete_existing_post_should_not_return_content() {

        String postId = "some generated id";
        String username = "user";
        String header = "Bearer token";

        when(jwtUtil.extractUsernameFromHeader(header)).thenReturn(username);

        webTestClient.delete()
                .uri(POSTS_BASE_PATH, postId)
                .header("Authorization", header)
                .exchange()
                .expectStatus()
                .isOk();

        assertAll(
                () -> verify(jwtUtil, times(1)).extractUsernameFromHeader(header),
                () -> verifyNoMoreInteractions(jwtUtil),
                () -> verify(postService, times(1)).deletePost(postId, username),
                () -> verifyNoMoreInteractions(postService),
                () -> verifyNoInteractions(modelMapper),
                () -> verifyNoInteractions(postObjectMapper));
    }

    @Test
    void when_delete_not_existing_user_post_should_return_error_response() {

        String postId = "some generated id";
        String notExistingUsername = "not existing username";
        String notExistingUserToken = "notExistingUserToken";
        String header = "Bearer " + notExistingUserToken;

        when(jwtUtil.extractUsernameFromHeader(header)).thenReturn(notExistingUsername);
        doThrow(new ForbiddenException("User can only change his own posts."))
                .when(postService).deletePost(postId, notExistingUsername);

        Mono<ErrorResponse> errorResponseMono = webTestClient.delete()
                .uri(POSTS_BASE_PATH, postId)
                .header("Authorization", header)
                .exchange()
                .expectStatus()
                .isUnauthorized()
                .returnResult(ErrorResponse.class)
                .getResponseBody()
                .single();

        StepVerifier.create(errorResponseMono)
                .thenConsumeWhile(
                        errorResponse -> {
                            assertAll(
                                    () -> assertEquals("User with name/email: '" + notExistingUsername + "' not found.",
                                            errorResponse.getErrors().get(0),
                                            () -> "should return error response with message: " +
                                                    "'User with name/email: '" + notExistingUsername + "' not found.'" + ", but was: "
                                                    + errorResponse.getErrors().get(0)),
                                    () -> assertEquals(1, errorResponse.getErrors().size(),
                                            () -> "should return error response with 1 message, but was: "
                                                    + errorResponse.getErrors().size()),
                                    () -> assertNotNull(errorResponse.getTimestamp(),
                                            () -> "should return error response with not null timestamp, but was: null"),
                                    () -> assertEquals(401, errorResponse.getStatus(),
                                            () -> "should return error response with 401 status, but was: "
                                                    + errorResponse.getStatus()),
                                    () -> verify(jwtUtil, times(1)).extractUsernameFromHeader(header),
                                    () -> verifyNoMoreInteractions(jwtUtil),
                                    () -> verify(postService, times(1)).deletePost(postId, notExistingUsername),
                                    () -> verifyNoMoreInteractions(postService),
                                    () -> verifyNoInteractions(modelMapper),
                                    () -> verifyNoInteractions(postObjectMapper));
                            return true;
                        }
                ).verifyComplete();
    }

    @Test
    void when_delete_not_existing_post_should_return_error_response() {

        String notExistingPostId = "not existing id";
        String username = "user";
        String header = "Bearer token";

        doThrow(new ResourceNotFoundException("Post", notExistingPostId)).when(postService).deletePost(notExistingPostId, username);

        Mono<ErrorResponse> errorResponseMono = webTestClient.delete()
                .uri(POSTS_BASE_PATH, notExistingPostId)
                .header("Authorization", header)
                .exchange()
                .expectStatus()
                .isNotFound()
                .returnResult(ErrorResponse.class)
                .getResponseBody()
                .single();

        StepVerifier.create(errorResponseMono)
                .thenConsumeWhile(
                        errorResponse -> {
                            assertAll(
                                    () -> assertEquals("Post with id: '" + notExistingPostId + "' not found.",
                                            errorResponse.getErrors().get(0),
                                            () -> "should return error response with message: " +
                                                    "'Post with id: '" + notExistingPostId + "' not found.'" + ", but was: "
                                                    + errorResponse.getErrors().get(0)),
                                    () -> assertEquals(1, errorResponse.getErrors().size(),
                                            () -> "should return error response with 1 message, but was: "
                                                    + errorResponse.getErrors().size()),
                                    () -> assertNotNull(errorResponse.getTimestamp(),
                                            () -> "should return error response with not null timestamp, but was: null"),
                                    () -> assertEquals(404, errorResponse.getStatus(),
                                            () -> "should return error response with 404 status, but was: "
                                                    + errorResponse.getStatus()),
                                    () -> verify(jwtUtil, times(1)).extractUsernameFromHeader(header),
                                    () -> verifyNoMoreInteractions(jwtUtil),
                                    () -> verify(postService, times(1))
                                            .deletePost(notExistingPostId, username),
                                    () -> verifyNoMoreInteractions(postService),
                                    () -> verifyNoInteractions(modelMapper),
                                    () -> verifyNoInteractions(postObjectMapper));
                            return true;
                        }
                ).verifyComplete();
    }

    @Test
    void when_delete_some_other_user_post_should_return_error_response() {

        String notExistingPostId = "not existing id";
        String username = "user";
        String header = "Bearer token";

        doThrow(new ForbiddenException("User can only change his own posts."))
                .when(postService).deletePost(notExistingPostId, username);

        Mono<ErrorResponse> errorResponseMono = webTestClient.delete()
                .uri(POSTS_BASE_PATH, notExistingPostId)
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
                            assertAll(
                                    () -> assertEquals("User can only change his own posts.",
                                            errorResponse.getErrors().get(0),
                                            () -> "should return error response with message: " +
                                                    "'User can only change his own posts.'" + ", but was: "
                                                    + errorResponse.getErrors().get(0)),
                                    () -> assertEquals(1, errorResponse.getErrors().size(),
                                            () -> "should return error response with 1 message, but was: "
                                                    + errorResponse.getErrors().size()),
                                    () -> assertNotNull(errorResponse.getTimestamp(),
                                            () -> "should return error response with not null timestamp, but was: null"),
                                    () -> assertEquals(403, errorResponse.getStatus(),
                                            () -> "should return error response with 403 status, but was: "
                                                    + errorResponse.getStatus()),
                                    () -> verify(jwtUtil, times(1)).extractUsernameFromHeader(header),
                                    () -> verifyNoMoreInteractions(jwtUtil),
                                    () -> verify(postService, times(1))
                                            .deletePost(notExistingPostId, username),
                                    () -> verifyNoMoreInteractions(postService),
                                    () -> verifyNoInteractions(modelMapper),
                                    () -> verifyNoInteractions(postObjectMapper));
                            return true;
                        }
                ).verifyComplete();
    }
}
