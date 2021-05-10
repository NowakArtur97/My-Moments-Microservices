package com.nowakArtur97.myMoments.commentService.feature.comment;

import com.nowakArtur97.myMoments.commentService.common.model.ErrorResponse;
import com.nowakArtur97.myMoments.commentService.common.util.JwtUtil;
import com.nowakArtur97.myMoments.commentService.exception.ForbiddenException;
import com.nowakArtur97.myMoments.commentService.exception.ResourceNotFoundException;
import com.nowakArtur97.myMoments.commentService.testUtil.enums.ObjectType;
import com.nowakArtur97.myMoments.commentService.testUtil.generator.NameWithSpacesGenerator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("CommentController_Tests")
class CommentUpdateControllerTest {

    @LocalServerPort
    private int serverPort;

    @Value("${my-moments.default-user-role:USER_ROLE}")
    private String defaultUserRole;

    private static String COMMENTS_BASE_PATH;

    @MockBean
    private CommentService commentService;

    @MockBean
    private ModelMapper modelMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private WebTestClient webTestClient;

    private String token;

    private static final String commentId = "commentId";
    private static final String relatedPostId = "postId";

    private static CommentTestBuilder commentTestBuilder;

    @BeforeAll
    static void setUpBuilders() {

        commentTestBuilder = new CommentTestBuilder();

        COMMENTS_BASE_PATH = "/api/v1/posts/" + relatedPostId + "/comments/" + commentId;
    }

    @BeforeEach
    void setUp() {

        webTestClient = webTestClient
                .mutate()
                .responseTimeout(Duration.ofMillis(30000))
                .build();

        token = jwtUtil.generateToken(new User("user", "user",
                List.of(new SimpleGrantedAuthority(defaultUserRole))));
    }

    @Test
    void when_update_valid_comment_should_return_comment() {

        String author = "user";
        CommentDTO commentDTOExpected = (CommentDTO) commentTestBuilder.build(ObjectType.CREATE_DTO);
        CommentDocument commentDocumentExpected = (CommentDocument) commentTestBuilder.withId(commentId).withAuthor(author)
                .build(ObjectType.DOCUMENT);
        CommentModel commentModelExpected = (CommentModel) commentTestBuilder.withId(commentId).withAuthor(author)
                .build(ObjectType.MODEL);

        when(commentService.updateComment(commentId, relatedPostId, author, commentDTOExpected))
                .thenReturn(Mono.just(commentDocumentExpected));
        when(modelMapper.map(commentDocumentExpected, CommentModel.class)).thenReturn(commentModelExpected);

        Mono<CommentModel> commentModelMono = webTestClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path(COMMENTS_BASE_PATH)
                        .build(commentId, relatedPostId))
                .bodyValue(commentDTOExpected)
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(CommentModel.class)
                .getResponseBody()
                .single();

        StepVerifier.create(commentModelMono)
                .thenConsumeWhile(
                        commentModelActual -> {
                            assertAll(
                                    () -> assertNotNull(commentModelActual.getId(),
                                            () -> "should return comment with not null id, but was: null"),
                                    () -> assertEquals(commentModelExpected.getContent(), commentModelActual.getContent(),
                                            () -> "should return comment with content: " + commentModelExpected.getContent()
                                                    + ", but was: " + commentModelActual.getContent()),
                                    () -> assertEquals(commentModelExpected.getAuthor(), commentModelActual.getAuthor(),
                                            () -> "should return comment with author: " + commentModelExpected.getAuthor()
                                                    + ", but was: " + commentModelActual.getAuthor()),
                                    () -> assertEquals(commentModelExpected.getCreateDate().toString(),
                                            commentModelActual.getCreateDate().toString(),
                                            () -> "should return comment with create date: "
                                                    + commentModelExpected.getCreateDate()
                                                    + ", but was: " + commentModelActual.getCreateDate()),
                                    () -> assertEquals(commentModelExpected.getModifyDate().toString(),
                                            commentModelActual.getModifyDate().toString(),
                                            () -> "should return comment with modify date: "
                                                    + commentModelExpected.getModifyDate()
                                                    + ", but was: " + commentModelActual.getModifyDate()),
                                    () -> verify(commentService, times(1))
                                            .updateComment(commentId, relatedPostId, author, commentDTOExpected),
                                    () -> verifyNoMoreInteractions(commentService),
                                    () -> verify(modelMapper, times(1)).map(commentDocumentExpected,
                                            CommentModel.class),
                                    () -> verifyNoMoreInteractions(modelMapper));
                            return true;
                        }
                ).verifyComplete();
    }

    @ParameterizedTest(name = "{index}: For Comment content: {0}")
    @EmptySource
    @ValueSource(strings = {" "})
    void when_update_comment_without_content_should_return_error_response(String invalidContent) {

        CommentDTO commentDTOExpected = (CommentDTO) commentTestBuilder.withContent(invalidContent)
                .build(ObjectType.CREATE_DTO);

        Mono<ErrorResponse> errorResponseMono = webTestClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path(COMMENTS_BASE_PATH)
                        .build(commentId, relatedPostId))
                .bodyValue(commentDTOExpected)
                .header("Authorization", "Bearer " + token)
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
                                    () -> assertEquals("Content cannot be blank.",
                                            errorResponse.getErrors().get(0),
                                            () -> "should return error response with message: "
                                                    + "'Content cannot be blank.'" + ", but was: "
                                                    + errorResponse.getErrors().get(0)),
                                    () -> assertEquals(1, errorResponse.getErrors().size(),
                                            () -> "should return error response with 1 message, but was: "
                                                    + errorResponse.getErrors().size()),
                                    () -> assertNotNull(errorResponse.getTimestamp(),
                                            () -> "should return error response with not null timestamp, but was: null"),
                                    () -> assertEquals(400, errorResponse.getStatus(),
                                            () -> "should return error response with 400 status, but was: "
                                                    + errorResponse.getStatus()),
                                    () -> verifyNoInteractions(commentService),
                                    () -> verifyNoInteractions(modelMapper));
                            return true;
                        }
                ).verifyComplete();
    }

    @Test
    void when_update_comment_with_too_long_content_should_return_error_response() {

        CommentDTO commentDTOExpected = (CommentDTO) commentTestBuilder.withContent("content".repeat(34))
                .build(ObjectType.CREATE_DTO);

        Mono<ErrorResponse> errorResponseMono = webTestClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path(COMMENTS_BASE_PATH)
                        .build(commentId, relatedPostId))
                .bodyValue(commentDTOExpected)
                .header("Authorization", "Bearer " + token)
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
                                    () -> assertEquals("Content cannot be longer than 200.",
                                            errorResponse.getErrors().get(0),
                                            () -> "should return error response with message: "
                                                    + "'Content cannot be longer than 200.'" + ", but was: "
                                                    + errorResponse.getErrors().get(0)),
                                    () -> assertEquals(1, errorResponse.getErrors().size(),
                                            () -> "should return error response with 1 message, but was: "
                                                    + errorResponse.getErrors().size()),
                                    () -> assertNotNull(errorResponse.getTimestamp(),
                                            () -> "should return error response with not null timestamp, but was: null"),
                                    () -> assertEquals(400, errorResponse.getStatus(),
                                            () -> "should return error response with 400 status, but was: "
                                                    + errorResponse.getStatus()),
                                    () -> verifyNoInteractions(commentService),
                                    () -> verifyNoInteractions(modelMapper));
                            return true;
                        }
                ).verifyComplete();
    }

    @Test
    void when_update_comment_by_not_existing_user_should_return_error_response() {

        String author = "user";
        CommentDTO commentDTOExpected = (CommentDTO) commentTestBuilder.build(ObjectType.CREATE_DTO);

        when(commentService.updateComment(commentId, relatedPostId, author, commentDTOExpected))
                .thenReturn(Mono.error(new UsernameNotFoundException("User with name: '" + author + "' not found.")));

        Mono<ErrorResponse> errorResponseMono = webTestClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path(COMMENTS_BASE_PATH)
                        .build(commentId, relatedPostId))
                .bodyValue(commentDTOExpected)
                .header("Authorization", "Bearer " + token)
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
                                    () -> assertEquals("User with name: '" + author + "' not found.",
                                            errorResponse.getErrors().get(0),
                                            () -> "should return error response with message: "
                                                    + "'User with name: '" + author + "' not found.'" + ", but was: "
                                                    + errorResponse.getErrors().get(0)),
                                    () -> assertEquals(1, errorResponse.getErrors().size(),
                                            () -> "should return error response with 1 message, but was: "
                                                    + errorResponse.getErrors().size()),
                                    () -> assertNotNull(errorResponse.getTimestamp(),
                                            () -> "should return error response with not null timestamp, but was: null"),
                                    () -> assertEquals(401, errorResponse.getStatus(),
                                            () -> "should return error response with 401 status, but was: "
                                                    + errorResponse.getStatus()),
                                    () -> verify(commentService, times(1))
                                            .updateComment(commentId, relatedPostId, author, commentDTOExpected),
                                    () -> verifyNoMoreInteractions(commentService),
                                    () -> verifyNoInteractions(modelMapper));
                            return true;
                        }
                ).verifyComplete();
    }

    @Test
    void when_update_not_existing_comment_should_return_error_response() {

        String author = "user";
        CommentDTO commentDTOExpected = (CommentDTO) commentTestBuilder.build(ObjectType.CREATE_DTO);

        when(commentService.updateComment(commentId, relatedPostId, author, commentDTOExpected))
                .thenReturn(Mono.error(new ResourceNotFoundException("Comment", commentId)));

        Mono<ErrorResponse> errorResponseMono = webTestClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path(COMMENTS_BASE_PATH)
                        .build(commentId, relatedPostId))
                .bodyValue(commentDTOExpected)
                .header("Authorization", "Bearer " + token)
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
                                    () -> verify(commentService, times(1))
                                            .updateComment(commentId, relatedPostId, author, commentDTOExpected),
                                    () -> verifyNoMoreInteractions(commentService),
                                    () -> verifyNoInteractions(modelMapper));
                            return true;
                        }
                ).verifyComplete();
    }

    @Test
    void when_update_not_existing_comment_on_specific_post_should_return_error_response() {

        String author = "user";
        CommentDTO commentDTOExpected = (CommentDTO) commentTestBuilder.build(ObjectType.CREATE_DTO);

        when(commentService.updateComment(commentId, relatedPostId, author, commentDTOExpected))
                .thenReturn(Mono.error(new ResourceNotFoundException("Comment with commentId: '" + commentId
                        + "' in the post with id: '" + relatedPostId + "' not found.")));

        Mono<ErrorResponse> errorResponseMono = webTestClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path(COMMENTS_BASE_PATH)
                        .build(commentId, relatedPostId))
                .bodyValue(commentDTOExpected)
                .header("Authorization", "Bearer " + token)
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
                                                    + "' in the post with id: '" + relatedPostId + "' not found.'" + ", but was: "
                                                    + errorResponse.getErrors().get(0)),
                                    () -> assertEquals(1, errorResponse.getErrors().size(),
                                            () -> "should return error response with 1 message, but was: "
                                                    + errorResponse.getErrors().size()),
                                    () -> assertNotNull(errorResponse.getTimestamp(),
                                            () -> "should return error response with not null timestamp, but was: null"),
                                    () -> assertEquals(404, errorResponse.getStatus(),
                                            () -> "should return error response with 404 status, but was: "
                                                    + errorResponse.getStatus()),
                                    () -> verify(commentService, times(1))
                                            .updateComment(commentId, relatedPostId, author, commentDTOExpected),
                                    () -> verifyNoMoreInteractions(commentService),
                                    () -> verifyNoInteractions(modelMapper));
                            return true;
                        }
                ).verifyComplete();
    }

    @Test
    void when_update_some_other_user_comment_should_return_error_response() {

        String author = "user";
        CommentDTO commentDTOExpected = (CommentDTO) commentTestBuilder.build(ObjectType.CREATE_DTO);

        when(commentService.updateComment(commentId, relatedPostId, author, commentDTOExpected))
                .thenReturn(Mono.error(new ForbiddenException("User can only change his own comments.")));

        Mono<ErrorResponse> errorResponseMono = webTestClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path(COMMENTS_BASE_PATH)
                        .build(commentId, relatedPostId))
                .bodyValue(commentDTOExpected)
                .header("Authorization", "Bearer " + token)
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
                                    () -> verify(commentService, times(1))
                                            .updateComment(commentId, relatedPostId, author, commentDTOExpected),
                                    () -> verifyNoMoreInteractions(commentService),
                                    () -> verifyNoInteractions(modelMapper));
                            return true;
                        }
                ).verifyComplete();
    }

    @Test
    void when_update_comment_without_token_should_return_error_response() {

        CommentDTO commentDTOExpected = (CommentDTO) commentTestBuilder.build(ObjectType.CREATE_DTO);

        Mono<ErrorResponse> errorResponseMono = webTestClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path(COMMENTS_BASE_PATH)
                        .build(commentId, relatedPostId))
                .bodyValue(commentDTOExpected)
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
                                    () -> verifyNoInteractions(commentService),
                                    () -> verifyNoInteractions(modelMapper));
                            return true;
                        }
                ).verifyComplete();
    }
}
