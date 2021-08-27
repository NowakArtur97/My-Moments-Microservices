package com.nowakArtur97.myMoments.postService.feature.resource;


import com.nowakArtur97.myMoments.postService.advice.ErrorResponse;
import com.nowakArtur97.myMoments.postService.exception.ForbiddenException;
import com.nowakArtur97.myMoments.postService.exception.ResourceNotFoundException;
import com.nowakArtur97.myMoments.postService.feature.PostTestBuilder;
import com.nowakArtur97.myMoments.postService.feature.document.PostDocument;
import com.nowakArtur97.myMoments.postService.feature.document.PostService;
import com.nowakArtur97.myMoments.postService.jwt.JwtUtil;
import com.nowakArtur97.myMoments.postService.testUtil.enums.ObjectType;
import com.nowakArtur97.myMoments.postService.testUtil.generator.NameWithSpacesGenerator;
import com.nowakArtur97.myMoments.postService.testUtil.mapper.ObjectTestMapper;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("PostController_Tests")
class PostUpdateControllerTest {

    private final String POSTS_BASE_PATH = "/api/v1/posts/{id}";

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
    }

    @Test
    void when_update_valid_post_should_return_post() {

        String postId = "postId";
        String updatedCaption = "updated caption";
        String header = "Bearer token";
        String author = "user";

        byte[] imageBytesExpected = "image.jpg".getBytes();
        Binary imageExpected = new Binary(BsonBinarySubType.BINARY, imageBytesExpected);
        PostDTO postDTOExpected = (PostDTO) postTestBuilder.withBinary(List.of(imageExpected)).build(ObjectType.CREATE_DTO);
        String postAsString = ObjectTestMapper.asJsonString(postDTOExpected);
        PostDocument postDocumentExpected = (PostDocument) postTestBuilder.withId(postId).withAuthor(author)
                .withCaption(updatedCaption).withBinary(List.of(imageExpected)).build(ObjectType.DOCUMENT);
        PostModel postModelExpected = (PostModel) postTestBuilder.withId(postId).withAuthor(author).withCaption(updatedCaption)
                .withBytes(List.of(imageBytesExpected)).build(ObjectType.MODEL);

        MultiValueMap<String, Object> objectMultiValueMap = new LinkedMultiValueMap<>();
        objectMultiValueMap.add("post", postAsString);
        objectMultiValueMap.add("photos",
                new ClassPathResource("example.jpg", this.getClass().getClassLoader()));

        when(jwtUtil.extractUsernameFromHeader(header)).thenReturn(author);
        when(postObjectMapper.getPostDTOFromString(any(String.class), any(Flux.class))).thenReturn(Mono.just(postDTOExpected));
        when(postService.updatePost(postId, author, postDTOExpected)).thenReturn(Mono.just(postDocumentExpected));
        when(modelMapper.map(postDocumentExpected, PostModel.class)).thenReturn(postModelExpected);

        Mono<PostModel> postModelMono = webTestClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path(POSTS_BASE_PATH)
                        .build(postId))
                .body(BodyInserters.fromMultipartData(objectMultiValueMap))
                .header("Authorization", header)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(PostModel.class)
                .getResponseBody()
                .single();

        StepVerifier.create(postModelMono)
                .thenConsumeWhile(
                        postModelActual -> {
                            assertAll(
                                    () -> assertEquals(postModelExpected.getId(), postModelActual.getId(),
                                            () -> "should return post with id: " + postModelExpected.getId()
                                                    + ", but was: " + postModelActual.getId()),
                                    () -> assertEquals(postModelExpected.getCaption(), postModelActual.getCaption(),
                                            () -> "should return post with caption: " + postModelExpected.getCaption()
                                                    + ", but was: " + postModelActual.getCaption()),
                                    () -> assertEquals(postModelExpected.getPhotos().size(), postModelActual.getPhotos().size(),
                                            () -> "should return post with photos size: " + postModelExpected.getPhotos().size()
                                                    + ", but was: " + postModelActual.getPhotos().size()),
                                    () -> assertEquals(postModelExpected.getAuthor(), postModelActual.getAuthor(),
                                            () -> "should return post with author: " + postModelExpected.getAuthor()
                                                    + ", but was: " + postModelActual.getAuthor()),
                                    () -> verify(jwtUtil, times(1)).extractUsernameFromHeader(header),
                                    () -> verifyNoMoreInteractions(jwtUtil),
                                    () -> verify(postObjectMapper, times(1))
                                            .getPostDTOFromString(any(String.class), any(Flux.class)),
                                    () -> verifyNoMoreInteractions(postObjectMapper),
                                    () -> verify(postService, times(1))
                                            .updatePost(postId, author, postDTOExpected),
                                    () -> verifyNoMoreInteractions(postService),
                                    () -> verify(modelMapper, times(1))
                                            .map(postDocumentExpected, PostModel.class),
                                    () -> verifyNoMoreInteractions(postObjectMapper));
                            return true;
                        }
                ).verifyComplete();
    }

    @Test
    void when_update_valid_post_without_caption_should_return_post_with_empty_caption() {

        String postId = "postId";
        String header = "Bearer token";
        String author = "user";

        byte[] imageBytesExpected = "image.jpg".getBytes();
        Binary imageExpected = new Binary(BsonBinarySubType.BINARY, imageBytesExpected);
        String emptyCaption = "";
        PostDTO postDTOExpectedToString = (PostDTO) postTestBuilder.withCaption(null).withBinary(List.of(imageExpected))
                .build(ObjectType.CREATE_DTO);
        PostDTO postDTOExpected = (PostDTO) postTestBuilder.withCaption("").build(ObjectType.CREATE_DTO);
        String postAsString = ObjectTestMapper.asJsonString(postDTOExpectedToString);
        PostDocument postDocumentExpected = (PostDocument) postTestBuilder.withId(postId).withAuthor(author)
                .withCaption("").withBinary(List.of(imageExpected)).build(ObjectType.DOCUMENT);
        PostModel postModelExpected = (PostModel) postTestBuilder.withId(postId).withAuthor("user").withCaption("")
                .withBytes(List.of(imageBytesExpected)).build(ObjectType.MODEL);

        MultiValueMap<String, Object> objectMultiValueMap = new LinkedMultiValueMap<>();
        objectMultiValueMap.add("post", postAsString);
        objectMultiValueMap.add("photos",
                new ClassPathResource("example.jpg", this.getClass().getClassLoader()));

        when(jwtUtil.extractUsernameFromHeader(header)).thenReturn(author);
        when(postObjectMapper.getPostDTOFromString(any(String.class), any(Flux.class))).thenReturn(Mono.just(postDTOExpected));
        when(postService.updatePost(postId, author, postDTOExpected)).thenReturn(Mono.just(postDocumentExpected));
        when(modelMapper.map(postDocumentExpected, PostModel.class)).thenReturn(postModelExpected);

        Mono<PostModel> postModelMono = webTestClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path(POSTS_BASE_PATH)
                        .build(postId))
                .body(BodyInserters.fromMultipartData(objectMultiValueMap))
                .header("Authorization", header)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(PostModel.class)
                .getResponseBody()
                .single();

        StepVerifier.create(postModelMono)
                .thenConsumeWhile(
                        postModelActual -> {
                            assertAll(
                                    () -> assertEquals(postModelExpected.getId(), postModelActual.getId(),
                                            () -> "should return post with id: " + postModelExpected.getId()
                                                    + ", but was: " + postModelActual.getId()),
                                    () -> assertEquals(emptyCaption, postModelActual.getCaption(),
                                            () -> "should return post with caption: " + emptyCaption + ", but was: "
                                                    + postModelActual.getCaption()),
                                    () -> assertEquals(postModelExpected.getPhotos().size(), postModelActual.getPhotos().size(),
                                            () -> "should return post with photos size: " + postModelExpected.getPhotos().size()
                                                    + ", but was: " + postModelActual.getPhotos().size()),
                                    () -> assertEquals(postModelExpected.getAuthor(), postModelActual.getAuthor(),
                                            () -> "should return post with author: " + postModelExpected.getAuthor()
                                                    + ", but was: " + postModelActual.getAuthor()),
                                    () -> verify(jwtUtil, times(1)).extractUsernameFromHeader(header),
                                    () -> verifyNoMoreInteractions(jwtUtil),
                                    () -> verify(postObjectMapper, times(1))
                                            .getPostDTOFromString(any(String.class), any(Flux.class)),
                                    () -> verifyNoMoreInteractions(postObjectMapper),
                                    () -> verify(postService, times(1))
                                            .updatePost(postId, author, postDTOExpected),
                                    () -> verifyNoMoreInteractions(postService),
                                    () -> verify(modelMapper, times(1))
                                            .map(postDocumentExpected, PostModel.class),
                                    () -> verifyNoMoreInteractions(postObjectMapper));
                            return true;
                        }
                ).verifyComplete();
    }

    @Test
    void when_update_not_existing_post_should_return_error_response() {

        String header = "Bearer token";
        String author = "user";

        String notExistingPostId = "not existing id";
        Binary imageExpected = new Binary(BsonBinarySubType.BINARY, "image.jpg".getBytes());
        PostDTO postDTOExpected = (PostDTO) postTestBuilder.withCaption(null).withBinary(List.of(imageExpected))
                .build(ObjectType.CREATE_DTO);
        String postAsString = ObjectTestMapper.asJsonString(postDTOExpected);

        MultiValueMap<String, Object> objectMultiValueMap = new LinkedMultiValueMap<>();
        objectMultiValueMap.add("post", postAsString);
        objectMultiValueMap.add("photos",
                new ClassPathResource("example.jpg", this.getClass().getClassLoader()));

        when(jwtUtil.extractUsernameFromHeader(header)).thenReturn(author);
        when(postObjectMapper.getPostDTOFromString(any(String.class), any(Flux.class))).thenReturn(Mono.just(postDTOExpected));
        doThrow(new ResourceNotFoundException("Post", notExistingPostId))
                .when(postService).updatePost(notExistingPostId, author, postDTOExpected);

        Mono<ErrorResponse> errorResponseMono = webTestClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path(POSTS_BASE_PATH)
                        .build(notExistingPostId))
                .body(BodyInserters.fromMultipartData(objectMultiValueMap))
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
                                    () -> verify(postObjectMapper, times(1))
                                            .getPostDTOFromString(any(String.class), any(Flux.class)),
                                    () -> verifyNoMoreInteractions(postObjectMapper),
                                    () -> verify(postService, times(1))
                                            .updatePost(notExistingPostId, author, postDTOExpected),
                                    () -> verifyNoMoreInteractions(postService),
                                    () -> verifyNoInteractions(modelMapper));
                            return true;
                        }
                ).verifyComplete();
    }

    @Test
    void when_update_other_user_post_should_return_error_response() {

        String postId = "postId";
        String header = "Bearer token";
        String notExistingUsername = "user";

        PostDTO postDTOExpected = (PostDTO) postTestBuilder.withAuthor(notExistingUsername).withCaption(null)
                .build(ObjectType.CREATE_DTO);
        String postAsString = ObjectTestMapper.asJsonString(postDTOExpected);

        MultiValueMap<String, Object> objectMultiValueMap = new LinkedMultiValueMap<>();
        objectMultiValueMap.add("post", postAsString);
        objectMultiValueMap.add("photos",
                new ClassPathResource("example.jpg", this.getClass().getClassLoader()));

        when(jwtUtil.extractUsernameFromHeader(header)).thenReturn(notExistingUsername);
        when(postObjectMapper.getPostDTOFromString(any(String.class), any(Flux.class))).thenReturn(Mono.just(postDTOExpected));
        doThrow(new ForbiddenException("User can only change his own posts."))
                .when(postService).updatePost(postId, notExistingUsername, postDTOExpected);

        Mono<ErrorResponse> errorResponseMono = webTestClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path(POSTS_BASE_PATH)
                        .build(postId))
                .body(BodyInserters.fromMultipartData(objectMultiValueMap))
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
                                    () -> verify(postObjectMapper, times(1))
                                            .getPostDTOFromString(any(String.class), any(Flux.class)),
                                    () -> verifyNoMoreInteractions(postObjectMapper),
                                    () -> verify(postService, times(1))
                                            .updatePost(postId, notExistingUsername, postDTOExpected),
                                    () -> verifyNoMoreInteractions(postService),
                                    () -> verifyNoInteractions(modelMapper));
                            return true;
                        }
                ).verifyComplete();
    }
}
