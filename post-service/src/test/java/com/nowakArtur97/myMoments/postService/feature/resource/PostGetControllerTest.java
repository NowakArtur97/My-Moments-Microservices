package com.nowakArtur97.myMoments.postService.feature.resource;

import com.nowakArtur97.myMoments.postService.advice.ErrorResponse;
import com.nowakArtur97.myMoments.postService.feature.PostTestBuilder;
import com.nowakArtur97.myMoments.postService.feature.document.PostDocument;
import com.nowakArtur97.myMoments.postService.feature.document.PostService;
import com.nowakArtur97.myMoments.postService.jwt.JwtUtil;
import com.nowakArtur97.myMoments.postService.testUtil.enums.ObjectType;
import com.nowakArtur97.myMoments.postService.testUtil.generator.NameWithSpacesGenerator;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("PostController_Tests")
class PostGetControllerTest {

    @LocalServerPort
    private int serverPort;

    private String POSTS_BASE_PATH;
    private String AUTHENTICATED_USER_POSTS_BASE_PATH;
    private String POST_BY_ID_BASE_PATH;

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

    private static PostTestBuilder postTestBuilder;

    @BeforeAll
    static void setUpBuilder() {

        postTestBuilder = new PostTestBuilder();
    }

    @BeforeEach
    void setUp() {

        webTestClient = webTestClient
                .mutate()
                .responseTimeout(Duration.ofMillis(30000))
                .build();

        POSTS_BASE_PATH = "http://localhost:" + serverPort + "/api/v1/posts";
        AUTHENTICATED_USER_POSTS_BASE_PATH = POSTS_BASE_PATH + "/me";
        POST_BY_ID_BASE_PATH = POSTS_BASE_PATH + "/{id}";
    }

    @Test
    void when_find_existing_post_should_return_post() {

        String postId = "some generated id";
        String header = "Bearer token";

        CommentModel commentModelExpected = new CommentModel("id", "content", "author",
                LocalDateTime.now(), LocalDateTime.now());
        CommentModel commentModelExpected2 = new CommentModel("id2", "content2", "author2",
                LocalDateTime.now(), LocalDateTime.now());
        List<CommentModel> commentsExpected = List.of(commentModelExpected, commentModelExpected2);
        PostsCommentsModel postsCommentsModelExpected = new PostsCommentsModel();
        byte[] imageBytesExpected = "image.jpg".getBytes();
        Binary imageExpected = new Binary(BsonBinarySubType.BINARY, imageBytesExpected);
        PostDocument postDocumentExpected = (PostDocument) postTestBuilder.withId(postId).withBinary(List.of(imageExpected))
                .build(ObjectType.DOCUMENT);
        PostModelWithComments postModelExpected = (PostModelWithComments) postTestBuilder.withId(postId)
                .withComments(commentsExpected).withBytes(List.of(imageBytesExpected)).build(ObjectType.MODEL);

        when(postService.getCommentsByPostId(postId)).thenReturn(Mono.just(postsCommentsModelExpected));
        when(postService.findPostById(postId)).thenReturn(Mono.just(postDocumentExpected));
        when(modelMapper.map(postDocumentExpected, PostModelWithComments.class)).thenReturn(postModelExpected);

        Mono<PostModelWithComments> postModelMono = webTestClient.get()
                .uri(POST_BY_ID_BASE_PATH, postId)
                .header("Authorization", header)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(PostModelWithComments.class)
                .getResponseBody()
                .single();

        StepVerifier.create(postModelMono)
                .thenConsumeWhile(
                        postModelActual -> {
                            assertAll(
                                    () -> assertNotNull(postModelActual.getId(),
                                            () -> "should return post with not null id, but was: null"),
                                    () -> assertEquals(postModelExpected.getCaption(), postModelActual.getCaption(),
                                            () -> "should return post with caption: " + postModelExpected.getCaption()
                                                    + ", but was: " + postModelActual.getCaption()),
                                    () -> assertEquals(postModelExpected.getPhotos().size(), postModelActual.getPhotos().size(),
                                            () -> "should return post with photos size: " + postModelExpected.getPhotos().size()
                                                    + ", but was: " + postModelActual.getPhotos().size()),
                                    () -> assertEquals(postModelExpected.getAuthor(), postModelActual.getAuthor(),
                                            () -> "should return post with author: " + postModelExpected.getAuthor()
                                                    + ", but was: " + postModelActual.getAuthor()),
                                    () -> verify(postService, times(1)).getCommentsByPostId(postId),
                                    () -> verify(postService, times(1)).findPostById(postId),
                                    () -> verifyNoMoreInteractions(postService),
                                    () -> verify(modelMapper, times(1))
                                            .map(postDocumentExpected, PostModelWithComments.class),
                                    () -> verifyNoMoreInteractions(modelMapper),
                                    () -> verifyNoInteractions(jwtUtil),
                                    () -> verifyNoInteractions(postObjectMapper));
                            return true;
                        }
                ).verifyComplete();
    }

    @Test
    void when_find_not_existing_post_should_return_error_response() {

        String postId = "some generated id";
        String header = "Bearer token";

        when(postService.getCommentsByPostId(postId)).thenReturn(Mono.empty());
        when(postService.findPostById(postId)).thenReturn(Mono.empty());

        Mono<ErrorResponse> errorResponseMono = webTestClient.get()
                .uri(POST_BY_ID_BASE_PATH, postId)
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
                                    () -> assertEquals("Post with id: '" + postId + "' not found.",
                                            errorResponse.getErrors().get(0),
                                            () -> "should return error response with message: " +
                                                    "'Post with id: '" + postId + "' not found." + ", but was: "
                                                    + errorResponse.getErrors().get(0)),
                                    () -> assertEquals(1, errorResponse.getErrors().size(),
                                            () -> "should return error response with 1 message, but was: "
                                                    + errorResponse.getErrors().size()),
                                    () -> assertNotNull(errorResponse.getTimestamp(),
                                            () -> "should return error response with not null timestamp, but was: null"),
                                    () -> assertEquals(404, errorResponse.getStatus(),
                                            () -> "should return error response with 404 status, but was: "
                                                    + errorResponse.getStatus()),
                                    () -> verify(postService, times(1)).getCommentsByPostId(postId),
                                    () -> verify(postService, times(1)).findPostById(postId),
                                    () -> verifyNoMoreInteractions(postService),
                                    () -> verifyNoInteractions(jwtUtil),
                                    () -> verifyNoInteractions(modelMapper),
                                    () -> verifyNoInteractions(postObjectMapper));
                            return true;
                        }
                ).verifyComplete();
    }

    @Test
    void when_find_posts_by_authenticated_user_should_return_posts() {

        String username = "user";
        String header = "Bearer token";

        byte[] imageBytesExpected = "image.jpg".getBytes();
        byte[] imageBytesExpected2 = "image2.jpg".getBytes();
        byte[] imageBytesExpected3 = "image3.jpg".getBytes();
        Binary imageExpected = new Binary(BsonBinarySubType.BINARY, imageBytesExpected);
        Binary imageExpected2 = new Binary(BsonBinarySubType.BINARY, imageBytesExpected);
        Binary imageExpected3 = new Binary(BsonBinarySubType.BINARY, imageBytesExpected);
        PostDocument postDocumentExpected = (PostDocument) postTestBuilder.withAuthor(username)
                .withBinary(List.of(imageExpected, imageExpected2)).build(ObjectType.DOCUMENT);
        PostDocument postDocumentExpected2 = (PostDocument) postTestBuilder.withCaption("caption 2").withAuthor(username)
                .withBinary(List.of(imageExpected3)).build(ObjectType.DOCUMENT);
        PostModel postModelExpected = (PostModel) postTestBuilder.withAuthor(username)
                .withBytes(List.of(imageBytesExpected, imageBytesExpected2)).build(ObjectType.MODEL);
        PostModel postModelExpected2 = (PostModel) postTestBuilder.withCaption("caption 2").withAuthor(username)
                .withBytes(List.of(imageBytesExpected3)).build(ObjectType.MODEL);

        when(jwtUtil.extractUsernameFromHeader(header)).thenReturn(username);
        when(postService.findPostsByAuthor(username)).thenReturn(Flux.just(postDocumentExpected, postDocumentExpected2));
        when(modelMapper.map(postDocumentExpected, PostModel.class)).thenReturn(postModelExpected);
        when(modelMapper.map(postDocumentExpected2, PostModel.class)).thenReturn(postModelExpected2);

        Mono<UsersPostsModel> userPostsModelMono = webTestClient.get()
                .uri(AUTHENTICATED_USER_POSTS_BASE_PATH)
                .header("Authorization", header)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(UsersPostsModel.class)
                .getResponseBody()
                .single();

        StepVerifier.create(userPostsModelMono)
                .thenConsumeWhile(
                        userPostsModelActual -> {
                            PostModel postModelActual = userPostsModelActual.getPosts().get(0);
                            PostModel postModelActual2 = userPostsModelActual.getPosts().get(1);
                            assertAll(
                                    () -> assertNotNull(postModelActual.getId(),
                                            () -> "should return post with not null id, but was: null"),
                                    () -> assertEquals(postModelExpected.getCaption(), postModelActual.getCaption(),
                                            () -> "should return post with caption: " + postModelExpected.getCaption()
                                                    + ", but was: " + postModelActual.getCaption()),
                                    () -> assertEquals(postModelExpected.getPhotos().size(), postModelActual.getPhotos().size(),
                                            () -> "should return post with photos size: " + postModelExpected.getPhotos().size()
                                                    + ", but was: " + postModelActual.getPhotos().size()),
                                    () -> assertEquals(postModelExpected.getAuthor(), postModelActual.getAuthor(),
                                            () -> "should return post with author: " + postModelExpected.getAuthor()
                                                    + ", but was: " + postModelActual.getAuthor()),
                                    () -> assertNotNull(postModelActual2.getId(),
                                            () -> "should return post with not null id, but was: null"),
                                    () -> assertEquals(postModelExpected2.getCaption(), postModelActual2.getCaption(),
                                            () -> "should return post with caption: " + postModelExpected2.getCaption()
                                                    + ", but was: " + postModelActual2.getCaption()),
                                    () -> assertEquals(postModelExpected2.getPhotos().size(), postModelActual2.getPhotos().size(),
                                            () -> "should return post with photos size: " + postModelExpected2.getPhotos().size()
                                                    + ", but was: " + postModelActual2.getPhotos().size()),
                                    () -> assertEquals(postModelExpected2.getAuthor(), postModelActual2.getAuthor(),
                                            () -> "should return post with author: " + postModelExpected2.getAuthor()
                                                    + ", but was: " + postModelActual2.getAuthor()),
                                    () -> assertEquals(2, userPostsModelActual.getPosts().size(),
                                            () -> "should return: two posts, but was: "
                                                    + userPostsModelActual.getPosts().size()),
                                    () -> verify(jwtUtil, times(1)).extractUsernameFromHeader(header),
                                    () -> verifyNoMoreInteractions(jwtUtil),
                                    () -> verify(postService, times(1)).findPostsByAuthor(username),
                                    () -> verifyNoMoreInteractions(postService),
                                    () -> verify(modelMapper, times(1))
                                            .map(postDocumentExpected, PostModel.class),
                                    () -> verify(modelMapper, times(1))
                                            .map(postDocumentExpected2, PostModel.class),
                                    () -> verifyNoMoreInteractions(modelMapper),
                                    () -> verifyNoInteractions(postObjectMapper));
                            return true;
                        }
                ).verifyComplete();
    }

    @Test
    void when_find_posts_by_authenticated_user_without_posts_should_not_return_any_posts() {

        String username = "user";
        String header = "Bearer token";

        when(jwtUtil.extractUsernameFromHeader(header)).thenReturn(username);
        when(postService.findPostsByAuthor(username)).thenReturn(Flux.empty());

        Mono<UsersPostsModel> userPostsModelMono = webTestClient.get()
                .uri(AUTHENTICATED_USER_POSTS_BASE_PATH)
                .header("Authorization", header)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(UsersPostsModel.class)
                .getResponseBody()
                .single();

        StepVerifier.create(userPostsModelMono)
                .thenConsumeWhile(
                        userPostsModelActual -> {
                            assertAll(
                                    () -> assertTrue(userPostsModelActual.getPosts().isEmpty(),
                                            () -> "should return: zero posts, but was: "
                                                    + userPostsModelActual.getPosts().size()),
                                    () -> verify(jwtUtil, times(1)).extractUsernameFromHeader(header),
                                    () -> verifyNoMoreInteractions(jwtUtil),
                                    () -> verify(postService, times(1)).findPostsByAuthor(username),
                                    () -> verifyNoMoreInteractions(postService),
                                    () -> verifyNoInteractions(modelMapper),
                                    () -> verifyNoInteractions(postObjectMapper));
                            return true;
                        }
                ).verifyComplete();
    }

    @Test
    void when_find_posts_by_usernames_without_page_should_return_posts() {

        String username = "user";
        String header = "Bearer token";
        PageRequest defaultPage = PageRequest.of(0, 20);

        byte[] imageBytesExpected = "image.jpg".getBytes();
        byte[] imageBytesExpected2 = "image2.jpg".getBytes();
        Binary imageExpected = new Binary(BsonBinarySubType.BINARY, imageBytesExpected);
        Binary imageExpected2 = new Binary(BsonBinarySubType.BINARY, imageBytesExpected);
        PostDocument postDocumentExpected = (PostDocument) postTestBuilder.withAuthor(username)
                .withBinary(List.of(imageExpected)).build(ObjectType.DOCUMENT);
        PostDocument postDocumentExpected2 = (PostDocument) postTestBuilder.withCaption("caption 2").withAuthor(username)
                .withBinary(List.of(imageExpected2)).build(ObjectType.DOCUMENT);
        PostModel postModelExpected = (PostModel) postTestBuilder.withAuthor(username)
                .withBytes(List.of(imageBytesExpected)).build(ObjectType.MODEL);
        PostModel postModelExpected2 = (PostModel) postTestBuilder.withCaption("caption 2").withAuthor(username)
                .withBytes(List.of(imageBytesExpected2)).build(ObjectType.MODEL);

        when(postService.findPostsByAuthors(List.of(username), defaultPage))
                .thenReturn(Flux.just(postDocumentExpected, postDocumentExpected2));
        when(modelMapper.map(postDocumentExpected, PostModel.class)).thenReturn(postModelExpected);
        when(modelMapper.map(postDocumentExpected2, PostModel.class)).thenReturn(postModelExpected2);

        Mono<UsersPostsModel> userPostsModelMono = webTestClient.get()
                .uri(POSTS_BASE_PATH + "?usernames=" + username)
                .header("Authorization", header)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(UsersPostsModel.class)
                .getResponseBody()
                .single();

        StepVerifier.create(userPostsModelMono)
                .thenConsumeWhile(
                        userPostsModelActual -> {
                            PostModel postModelActual = userPostsModelActual.getPosts().get(0);
                            PostModel postModelActual2 = userPostsModelActual.getPosts().get(1);
                            assertAll(
                                    () -> assertNotNull(postModelActual.getId(),
                                            () -> "should return post with not null id, but was: null"),
                                    () -> assertEquals(postModelExpected.getCaption(), postModelActual.getCaption(),
                                            () -> "should return post with caption: " + postModelExpected.getCaption()
                                                    + ", but was: " + postModelActual.getCaption()),
                                    () -> assertEquals(postModelExpected.getPhotos().size(), postModelActual.getPhotos().size(),
                                            () -> "should return post with photos size: " + postModelExpected.getPhotos().size()
                                                    + ", but was: " + postModelActual.getPhotos().size()),
                                    () -> assertEquals(postModelExpected.getAuthor(), postModelActual.getAuthor(),
                                            () -> "should return post with author: " + postModelExpected.getAuthor()
                                                    + ", but was: " + postModelActual.getAuthor()),
                                    () -> assertNotNull(postModelActual2.getId(),
                                            () -> "should return post with not null id, but was: null"),
                                    () -> assertEquals(postModelExpected2.getCaption(), postModelActual2.getCaption(),
                                            () -> "should return post with caption: " + postModelExpected2.getCaption()
                                                    + ", but was: " + postModelActual2.getCaption()),
                                    () -> assertEquals(postModelExpected2.getPhotos().size(), postModelActual2.getPhotos().size(),
                                            () -> "should return post with photos size: " + postModelExpected2.getPhotos().size()
                                                    + ", but was: " + postModelActual2.getPhotos().size()),
                                    () -> assertEquals(postModelExpected2.getAuthor(), postModelActual2.getAuthor(),
                                            () -> "should return post with author: " + postModelExpected2.getAuthor()
                                                    + ", but was: " + postModelActual2.getAuthor()),
                                    () -> assertEquals(2, userPostsModelActual.getPosts().size(),
                                            () -> "should return: two posts, but was: "
                                                    + userPostsModelActual.getPosts().size()),
                                    () -> verify(postService, times(1))
                                            .findPostsByAuthors(List.of(username), defaultPage),
                                    () -> verifyNoMoreInteractions(postService),
                                    () -> verify(modelMapper, times(1))
                                            .map(postDocumentExpected, PostModel.class),
                                    () -> verify(modelMapper, times(1))
                                            .map(postDocumentExpected2, PostModel.class),
                                    () -> verifyNoMoreInteractions(modelMapper),
                                    () -> verifyNoInteractions(jwtUtil),
                                    () -> verifyNoInteractions(postObjectMapper));
                            return true;
                        }
                ).verifyComplete();
    }

    @Test
    void when_find_posts_by_usernames_with_page_should_return_posts() {

        String username = "user";
        String header = "Bearer token";
        PageRequest page = PageRequest.of(0, 10);

        byte[] imageBytesExpected = "image.jpg".getBytes();
        byte[] imageBytesExpected2 = "image2.jpg".getBytes();
        Binary imageExpected = new Binary(BsonBinarySubType.BINARY, imageBytesExpected);
        Binary imageExpected2 = new Binary(BsonBinarySubType.BINARY, imageBytesExpected);
        PostDocument postDocumentExpected = (PostDocument) postTestBuilder.withAuthor(username)
                .withBinary(List.of(imageExpected)).build(ObjectType.DOCUMENT);
        PostDocument postDocumentExpected2 = (PostDocument) postTestBuilder.withCaption("caption 2").withAuthor(username)
                .withBinary(List.of(imageExpected2)).build(ObjectType.DOCUMENT);
        PostModel postModelExpected = (PostModel) postTestBuilder.withAuthor(username)
                .withBytes(List.of(imageBytesExpected)).build(ObjectType.MODEL);
        PostModel postModelExpected2 = (PostModel) postTestBuilder.withCaption("caption 2").withAuthor(username)
                .withBytes(List.of(imageBytesExpected2)).build(ObjectType.MODEL);

        when(postService.findPostsByAuthors(List.of(username), page))
                .thenReturn(Flux.just(postDocumentExpected, postDocumentExpected2));
        when(modelMapper.map(postDocumentExpected, PostModel.class)).thenReturn(postModelExpected);
        when(modelMapper.map(postDocumentExpected2, PostModel.class)).thenReturn(postModelExpected2);

        Mono<UsersPostsModel> userPostsModelMono = webTestClient.get()
                .uri(POSTS_BASE_PATH + "?usernames=" + username + "&page=0&size=10")
                .header("Authorization", header)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(UsersPostsModel.class)
                .getResponseBody()
                .single();

        StepVerifier.create(userPostsModelMono)
                .thenConsumeWhile(
                        userPostsModelActual -> {
                            PostModel postModelActual = userPostsModelActual.getPosts().get(0);
                            PostModel postModelActual2 = userPostsModelActual.getPosts().get(1);
                            assertAll(
                                    () -> assertNotNull(postModelActual.getId(),
                                            () -> "should return post with not null id, but was: null"),
                                    () -> assertEquals(postModelExpected.getCaption(), postModelActual.getCaption(),
                                            () -> "should return post with caption: " + postModelExpected.getCaption()
                                                    + ", but was: " + postModelActual.getCaption()),
                                    () -> assertEquals(postModelExpected.getPhotos().size(), postModelActual.getPhotos().size(),
                                            () -> "should return post with photos size: " + postModelExpected.getPhotos().size()
                                                    + ", but was: " + postModelActual.getPhotos().size()),
                                    () -> assertEquals(postModelExpected.getAuthor(), postModelActual.getAuthor(),
                                            () -> "should return post with author: " + postModelExpected.getAuthor()
                                                    + ", but was: " + postModelActual.getAuthor()),
                                    () -> assertNotNull(postModelActual2.getId(),
                                            () -> "should return post with not null id, but was: null"),
                                    () -> assertEquals(postModelExpected2.getCaption(), postModelActual2.getCaption(),
                                            () -> "should return post with caption: " + postModelExpected2.getCaption()
                                                    + ", but was: " + postModelActual2.getCaption()),
                                    () -> assertEquals(postModelExpected2.getPhotos().size(), postModelActual2.getPhotos().size(),
                                            () -> "should return post with photos size: " + postModelExpected2.getPhotos().size()
                                                    + ", but was: " + postModelActual2.getPhotos().size()),
                                    () -> assertEquals(postModelExpected2.getAuthor(), postModelActual2.getAuthor(),
                                            () -> "should return post with author: " + postModelExpected2.getAuthor()
                                                    + ", but was: " + postModelActual2.getAuthor()),
                                    () -> assertEquals(2, userPostsModelActual.getPosts().size(),
                                            () -> "should return: two posts, but was: "
                                                    + userPostsModelActual.getPosts().size()),
                                    () -> verify(postService, times(1))
                                            .findPostsByAuthors(List.of(username), page),
                                    () -> verifyNoMoreInteractions(postService),
                                    () -> verify(modelMapper, times(1))
                                            .map(postDocumentExpected, PostModel.class),
                                    () -> verify(modelMapper, times(1))
                                            .map(postDocumentExpected2, PostModel.class),
                                    () -> verifyNoMoreInteractions(modelMapper),
                                    () -> verifyNoInteractions(jwtUtil),
                                    () -> verifyNoInteractions(postObjectMapper));
                            return true;
                        }
                ).verifyComplete();
    }

    @Test
    void when_find_posts_by_usernames_without_posts_should_not_return_any_posts() {

        String username = "user";
        String header = "Bearer token";
        PageRequest defaultPage = PageRequest.of(0, 20);

        when(postService.findPostsByAuthors(List.of(username), defaultPage)).thenReturn(Flux.empty());

        Mono<UsersPostsModel> userPostsModelMono = webTestClient.get()
                .uri(POSTS_BASE_PATH + "?usernames=" + username)
                .header("Authorization", header)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(UsersPostsModel.class)
                .getResponseBody()
                .single();

        StepVerifier.create(userPostsModelMono)
                .thenConsumeWhile(
                        userPostsModelActual -> {
                            assertAll(
                                    () -> assertTrue(userPostsModelActual.getPosts().isEmpty(),
                                            () -> "should return: zero posts, but was: "
                                                    + userPostsModelActual.getPosts().size()),
                                    () -> verify(postService, times(1))
                                            .findPostsByAuthors(List.of(username), defaultPage),
                                    () -> verifyNoMoreInteractions(postService),
                                    () -> verifyNoInteractions(jwtUtil),
                                    () -> verifyNoInteractions(modelMapper),
                                    () -> verifyNoInteractions(postObjectMapper));
                            return true;
                        }
                ).verifyComplete();
    }
}
