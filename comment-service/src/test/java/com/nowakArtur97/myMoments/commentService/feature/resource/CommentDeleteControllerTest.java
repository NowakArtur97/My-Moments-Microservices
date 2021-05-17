package com.nowakArtur97.myMoments.commentService.feature.resource;

import com.nowakArtur97.myMoments.commentService.advice.ErrorResponse;
import com.nowakArtur97.myMoments.commentService.jwt.JwtUtil;
import com.nowakArtur97.myMoments.commentService.exception.ForbiddenException;
import com.nowakArtur97.myMoments.commentService.exception.ResourceNotFoundException;
import com.nowakArtur97.myMoments.commentService.feature.document.CommentService;
import com.nowakArtur97.myMoments.commentService.testUtil.generator.NameWithSpacesGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("CommentController_Tests")
class CommentDeleteControllerTest {

    @Value("${my-moments.default-user-role:USER_ROLE}")
    private String defaultUserRole;
    
    private final String commentId = "commentId";
    private final String relatedPostId = "postId";
    private final String COMMENTS_BASE_PATH = "/api/v1/posts/" + relatedPostId + "/comments/" + commentId;

    @MockBean
    private CommentService commentService;

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
    }

    @Test
    void when_delete_valid_comment_should_not_return_content() {

        String header = "Bearer token";
        String username = "user";

        Void postDocumentVoid = mock(Void.class);
        when(jwtUtil.extractUsernameFromHeader(header)).thenReturn(username);
        when(commentService.deleteComment(commentId, relatedPostId, username)).thenReturn(Mono.just(postDocumentVoid));

        webTestClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path(COMMENTS_BASE_PATH)
                        .build(commentId, relatedPostId))
                .header("Authorization", header)
                .exchange()
                .expectStatus()
                .isOk();

        assertAll(
                () -> verify(jwtUtil, times(1)).extractUsernameFromHeader(header),
                () -> verifyNoMoreInteractions(jwtUtil),
                () -> verify(commentService, times(1)).deleteComment(commentId, relatedPostId, username),
                () -> verifyNoMoreInteractions(commentService),
                () -> verifyNoInteractions(modelMapper));
    }

    @Test
    void when_delete_not_existing_comment_should_return_error_response() {

        String header = "Bearer token";
        String username = "user";

        when(jwtUtil.extractUsernameFromHeader(header)).thenReturn(username);
        when(commentService.deleteComment(commentId, relatedPostId, username))
                .thenReturn(Mono.error(new ResourceNotFoundException("Comment", commentId)));

        Mono<ErrorResponse> errorResponseMono = webTestClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path(COMMENTS_BASE_PATH)
                        .build(commentId, relatedPostId))
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
                                    () -> assertEquals("Comment with id: '" + commentId + "' not found.",
                                            errorResponse.getErrors().get(0),
                                            () -> "should return error response with message: "
                                                    + "'Comment with id: '" + commentId + "' not found.'" + ", but was: "
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
                                    () -> verify(commentService, times(1))
                                            .deleteComment(commentId, relatedPostId, username),
                                    () -> verifyNoMoreInteractions(commentService),
                                    () -> verifyNoInteractions(modelMapper));
                            return true;
                        }
                ).verifyComplete();
    }

    @Test
    void when_delete_not_existing_comment_on_specific_post_should_return_error_response() {

        String header = "Bearer token";
        String username = "user";

        when(jwtUtil.extractUsernameFromHeader(header)).thenReturn(username);
        when(commentService.deleteComment(commentId, relatedPostId, username))
                .thenReturn(Mono.error(new ResourceNotFoundException("Comment with commentId: '" + commentId
                        + "' in the post with id: '" + relatedPostId + "' not found.")));

        Mono<ErrorResponse> errorResponseMono = webTestClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path(COMMENTS_BASE_PATH)
                        .build(commentId, relatedPostId))
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
                                    () -> assertEquals("Comment with commentId: '" + commentId
                                                    + "' in the post with id: '" + relatedPostId + "' not found.",
                                            errorResponse.getErrors().get(0),
                                            () -> "should return error response with message: "
                                                    + "'Comment with commentId: '" + commentId
                                                    + "' in the post with id: '" + relatedPostId + "' not found.'"
                                                    + ", but was: " + errorResponse.getErrors().get(0)),
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
                                    () -> verify(commentService, times(1))
                                            .deleteComment(commentId, relatedPostId, username),
                                    () -> verifyNoMoreInteractions(commentService),
                                    () -> verifyNoInteractions(modelMapper));
                            return true;
                        }
                ).verifyComplete();
    }

    @Test
    void when_delete_some_other_user_comment_should_return_error_response() {

        String header = "Bearer token";
        String username = "user";

        when(jwtUtil.extractUsernameFromHeader(header)).thenReturn(username);
        when(commentService.deleteComment(commentId, relatedPostId, username))
                .thenReturn(Mono.error(new ForbiddenException("User can only change his own comments.")));

        Mono<ErrorResponse> errorResponseMono = webTestClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path(COMMENTS_BASE_PATH)
                        .build(commentId, relatedPostId))
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
                                    () -> assertEquals("User can only change his own comments.",
                                            errorResponse.getErrors().get(0),
                                            () -> "should return error response with message: "
                                                    + "'User can only change his own comments.'" + ", but was: "
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
                                    () -> verify(commentService, times(1))
                                            .deleteComment(commentId, relatedPostId, username),
                                    () -> verifyNoMoreInteractions(commentService),
                                    () -> verifyNoInteractions(modelMapper));
                            return true;
                        }
                ).verifyComplete();
    }
}
