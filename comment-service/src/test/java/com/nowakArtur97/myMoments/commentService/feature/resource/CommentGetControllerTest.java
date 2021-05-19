package com.nowakArtur97.myMoments.commentService.feature.resource;

import com.nowakArtur97.myMoments.commentService.feature.CommentTestBuilder;
import com.nowakArtur97.myMoments.commentService.feature.document.CommentDocument;
import com.nowakArtur97.myMoments.commentService.feature.document.CommentService;
import com.nowakArtur97.myMoments.commentService.jwt.JwtUtil;
import com.nowakArtur97.myMoments.commentService.testUtil.enums.ObjectType;
import com.nowakArtur97.myMoments.commentService.testUtil.generator.NameWithSpacesGenerator;
import org.junit.jupiter.api.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("CommentController_Tests")
class CommentGetControllerTest {

    @LocalServerPort
    private int serverPort;

    private String COMMENTS_BASE_PATH;

    @MockBean
    private CommentService commentService;

    @MockBean
    private ModelMapper modelMapper;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private WebTestClient webTestClient;

    private static CommentTestBuilder commentTestBuilder;

    @BeforeAll
    static void setUpBuilder() {

        commentTestBuilder = new CommentTestBuilder();
    }

    @BeforeEach
    void setUp() {

        webTestClient = webTestClient
                .mutate()
                .responseTimeout(Duration.ofMillis(30000))
                .build();

        COMMENTS_BASE_PATH = "http://localhost:" + serverPort + "/api/v1/posts/{postId}/comments";
    }

    @Test
    void when_get_comment_by_post_id_should_return_comments() {

        String header = "Bearer token";
        String relatedPostId = "postId";

        CommentDocument commentDocumentExpected = (CommentDocument) commentTestBuilder.build(ObjectType.DOCUMENT);
        CommentDocument commentDocumentExpected2 = (CommentDocument) commentTestBuilder.withContent("content 2")
                .build(ObjectType.DOCUMENT);
        CommentModel commentModelExpected = (CommentModel) commentTestBuilder.build(ObjectType.MODEL);
        CommentModel commentModelExpected2 = (CommentModel) commentTestBuilder.withContent("content 2")
                .build(ObjectType.MODEL);

        when(commentService.findCommentsByRelatedPost(relatedPostId))
                .thenReturn(Flux.just(commentDocumentExpected, commentDocumentExpected2));
        when(modelMapper.map(commentDocumentExpected, CommentModel.class)).thenReturn(commentModelExpected);
        when(modelMapper.map(commentDocumentExpected2, CommentModel.class)).thenReturn(commentModelExpected2);

        Mono<PostsCommentsModel> postsCommentsModelMono = webTestClient.get()
                .uri(COMMENTS_BASE_PATH, relatedPostId)
                .header("Authorization", header)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(PostsCommentsModel.class)
                .getResponseBody()
                .single();

        StepVerifier.create(postsCommentsModelMono)
                .thenConsumeWhile(
                        postsCommentsModelActual -> {
                            CommentModel commentModelActual = postsCommentsModelActual.getComments().get(0);
                            CommentModel commentModelActual2 = postsCommentsModelActual.getComments().get(1);

                            assertAll(
                                    () -> assertNotNull(commentModelActual.getId(),
                                            () -> "should return comment with not null id, but was: null"),
                                    () -> assertEquals(commentModelExpected.getId(), commentModelActual.getId(),
                                            () -> "should return comment with id: " + commentModelExpected.getId()
                                                    + ", but was: " + commentModelActual.getId()),
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

                                    () -> assertNotNull(commentModelActual2.getId(),
                                            () -> "should return comment with not null id, but was: null"),
                                    () -> assertEquals(commentModelExpected2.getId(), commentModelActual2.getId(),
                                            () -> "should return comment with id: " + commentModelExpected2.getId()
                                                    + ", but was: " + commentModelActual2.getId()),
                                    () -> assertEquals(commentModelExpected2.getContent(), commentModelActual2.getContent(),
                                            () -> "should return comment with content: " + commentModelExpected2.getContent()
                                                    + ", but was: " + commentModelActual2.getContent()),
                                    () -> assertEquals(commentModelExpected2.getAuthor(), commentModelActual2.getAuthor(),
                                            () -> "should return comment with author: " + commentModelExpected2.getAuthor()
                                                    + ", but was: " + commentModelActual2.getAuthor()),
                                    () -> assertEquals(commentModelExpected2.getCreateDate().toString(),
                                            commentModelActual2.getCreateDate().toString(),
                                            () -> "should return comment with create date: "
                                                    + commentModelExpected2.getCreateDate()
                                                    + ", but was: " + commentModelActual2.getCreateDate()),
                                    () -> assertEquals(commentModelExpected2.getModifyDate().toString(),
                                            commentModelActual2.getModifyDate().toString(),
                                            () -> "should return comment with modify date: "
                                                    + commentModelExpected2.getModifyDate()
                                                    + ", but was: " + commentModelActual2.getModifyDate()),
                                    () -> verify(commentService, times(1))
                                            .findCommentsByRelatedPost(relatedPostId),
                                    () -> verifyNoMoreInteractions(commentService),
                                    () -> verify(modelMapper, times(1)).map(commentDocumentExpected,
                                            CommentModel.class),
                                    () -> verify(modelMapper, times(1)).map(commentDocumentExpected2,
                                            CommentModel.class),
                                    () -> verifyNoMoreInteractions(modelMapper),
                                    () -> verifyNoInteractions(jwtUtil));
                            return true;
                        }
                ).verifyComplete();
    }

    @Test
    void when_get_comment_by_post_id_but_found_nothing_should_return_empty_flux() {

        String header = "Bearer token";
        String relatedPostId = "postId";

        when(commentService.findCommentsByRelatedPost(relatedPostId)).thenReturn(Flux.empty());

        Mono<PostsCommentsModel> postsCommentsModelMono = webTestClient.get()
                .uri(COMMENTS_BASE_PATH, relatedPostId)
                .header("Authorization", header)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(PostsCommentsModel.class)
                .getResponseBody()
                .single();

        StepVerifier.create(postsCommentsModelMono)
                .thenConsumeWhile(
                        postsCommentsModelActual -> {
                            assertAll(
                                    () -> assertTrue(postsCommentsModelActual.getComments().isEmpty(),
                                            () -> "should return: zero comments, but was: "
                                                    + postsCommentsModelActual.getComments().size()),
                                    () -> verify(commentService, times(1))
                                            .findCommentsByRelatedPost(relatedPostId),
                                    () -> verifyNoMoreInteractions(commentService),
                                    () -> verifyNoInteractions(modelMapper),
                                    () -> verifyNoInteractions(jwtUtil));
                            return true;
                        }
                ).verifyComplete();
    }
}
