package com.nowakArtur97.myMoments.postService.feature.post;

import com.nowakArtur97.myMoments.postService.common.model.ErrorResponse;
import com.nowakArtur97.myMoments.postService.common.util.JwtUtil;
import com.nowakArtur97.myMoments.postService.exception.ForbiddenException;
import com.nowakArtur97.myMoments.postService.exception.ResourceNotFoundException;
import com.nowakArtur97.myMoments.postService.generator.NameWithSpacesGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("PostController_Tests")
class PostDeleteControllerTest {

    @LocalServerPort
    private int serverPort;

    @Value("${my-moments.default-user-role:USER_ROLE}")
    private String defaultUserRole;

    private String POSTS_BASE_PATH;

    @MockBean
    private PostService postService;

    @MockBean
    private PostObjectMapper postObjectMapper;

    @MockBean
    private ModelMapper modelMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private WebTestClient webTestClient;

    private String token;

    @BeforeEach
    void setUp() {

        webTestClient = webTestClient
                .mutate()
                .responseTimeout(Duration.ofMillis(30000))
                .build();

        token = jwtUtil.generateToken(new User("user", "user",
                List.of(new SimpleGrantedAuthority(defaultUserRole))));

        POSTS_BASE_PATH = "http://localhost:" + serverPort + "/api/v1/posts/{id}";
    }

    @Test
    void when_delete_existing_post_should_not_return_content() {

        String postId = "some generated id";
        String username = "user";
        String header = "Bearer " + token;

        Void postDocumentVoid = mock(Void.class);
        when(postService.deletePost(postId, username)).thenReturn(Mono.just(postDocumentVoid));

        webTestClient.delete()
                .uri(POSTS_BASE_PATH, postId)
                .header("Authorization", header)
                .exchange()
                .expectStatus()
                .isOk();

        assertAll(
                () -> verify(postService, times(1)).deletePost(postId, username),
                () -> verifyNoMoreInteractions(postService),
                () -> verifyNoInteractions(modelMapper),
                () -> verifyNoInteractions(postObjectMapper));
    }

    @Test
    void when_delete_post_without_token_should_return_error_response() {

        String postId = "some generated id";

        Mono<ErrorResponse> errorResponseMono = webTestClient.delete()
                .uri(POSTS_BASE_PATH, postId)
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
                                    () -> assertEquals("JWT token is missing in request headers.",
                                            errorResponse.getErrors().get(0),
                                            () -> "should return error response with message: " +
                                                    "'JWT token is missing in request headers.'" + ", but was: "
                                                    + errorResponse.getErrors().get(0)),
                                    () -> assertEquals(1, errorResponse.getErrors().size(),
                                            () -> "should return error response with 1 message, but was: "
                                                    + errorResponse.getErrors().size()),
                                    () -> assertNotNull(errorResponse.getTimestamp(),
                                            () -> "should return error response with not null timestamp, but was: null"),
                                    () -> assertEquals(401, errorResponse.getStatus(),
                                            () -> "should return error response with 401 status, but was: "
                                                    + errorResponse.getStatus()),
                                    () -> verifyNoInteractions(postService),
                                    () -> verifyNoInteractions(modelMapper),
                                    () -> verifyNoInteractions(postObjectMapper));
                            return true;
                        }
                ).verifyComplete();
    }

    @Test
    void when_delete_not_existing_user_post_should_return_error_response() {

        String postId = "some generated id";
        String notExistingUsername = "not existing username";
        String notExistingUserToken = jwtUtil.generateToken(new User(notExistingUsername, notExistingUsername,
                List.of(new SimpleGrantedAuthority("USER_ROLE"))));
        String header = "Bearer " + notExistingUserToken;

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
                                    () -> verifyNoInteractions(postService),
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
        String header = "Bearer " + token;

        doThrow(new ResourceNotFoundException("Post", notExistingPostId))
                .when(postService).deletePost(notExistingPostId, username);

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
        String header = "Bearer " + token;

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
