package com.nowakArtur97.myMoments.commentService.feature.comment;

import com.nowakArtur97.myMoments.commentService.common.model.ErrorResponse;
import com.nowakArtur97.myMoments.commentService.common.util.JwtUtil;
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
class CommentCreateControllerTest {

    @LocalServerPort
    private int serverPort;

    @Value("${my-moments.default-user-role:USER_ROLE}")
    private String defaultUserRole;

    private String COMMENTS_BASE_PATH;

    @MockBean
    private CommentService commentService;

    @MockBean
    private ModelMapper modelMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private WebTestClient webTestClient;

    private String token;

    private static CommentTestBuilder commentTestBuilder;

    @BeforeAll
    static void setUpBuilders() {

        commentTestBuilder = new CommentTestBuilder();
    }

    @BeforeEach
    void setUp() {

        webTestClient = webTestClient
                .mutate()
                .responseTimeout(Duration.ofMillis(30000))
                .build();

        token = jwtUtil.generateToken(new User("user", "user",
                List.of(new SimpleGrantedAuthority(defaultUserRole))));

        COMMENTS_BASE_PATH = "http://localhost:" + serverPort + "/api/v1/posts/{commentId}/comments";
    }

    @Test
    void when_create_valid_comment_should_return_comment() {

        String relatedPostId = "postId";
        String author = "user";
        CommentDTO commentDTOExpected = (CommentDTO) commentTestBuilder.build(ObjectType.CREATE_DTO);
        CommentDocument commentDocumentExpected = (CommentDocument) commentTestBuilder.withAuthor(author)
                .build(ObjectType.DOCUMENT);
        CommentModel commentModelExpected = (CommentModel) commentTestBuilder.withAuthor(author).build(ObjectType.MODEL);

        when(commentService.addComment(relatedPostId, author, commentDTOExpected))
                .thenReturn(Mono.just(commentDocumentExpected));
        when(modelMapper.map(commentDocumentExpected, CommentModel.class)).thenReturn(commentModelExpected);

        Mono<CommentModel> commentModelMono = webTestClient.post()
                .uri(COMMENTS_BASE_PATH, relatedPostId)
                .bodyValue(commentDTOExpected)
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus()
                .isCreated()
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
                                    () -> verify(commentService, times(1)).addComment(relatedPostId,
                                            author, commentDTOExpected),
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
    void when_add_comment_without_content_should_return_error_response(String invalidContent) {

        String relatedPostId = "postId";
        CommentDTO commentDTOExpected = (CommentDTO) commentTestBuilder.withContent(invalidContent).build(ObjectType.CREATE_DTO);

        Mono<ErrorResponse> errorResponseMono = webTestClient.post()
                .uri(COMMENTS_BASE_PATH, relatedPostId)
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
                                    () -> assertNotNull(errorResponse.getDateTime(),
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
    void when_add_comment_with_too_long_content_should_return_error_response() {

        String relatedPostId = "postId";
        CommentDTO commentDTOExpected = (CommentDTO) commentTestBuilder.withContent("content".repeat(34))
                .build(ObjectType.CREATE_DTO);

        Mono<ErrorResponse> errorResponseMono = webTestClient.post()
                .uri(COMMENTS_BASE_PATH, relatedPostId)
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
                                    () -> assertNotNull(errorResponse.getDateTime(),
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
    void when_add_comment_without_token_should_return_error_response() {

        String relatedPostId = "postId";
        CommentDTO commentDTOExpected = (CommentDTO) commentTestBuilder.build(ObjectType.CREATE_DTO);

        Mono<ErrorResponse> errorResponseMono = webTestClient.post()
                .uri(COMMENTS_BASE_PATH, relatedPostId)
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
                                    () -> assertNotNull(errorResponse.getDateTime(),
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
