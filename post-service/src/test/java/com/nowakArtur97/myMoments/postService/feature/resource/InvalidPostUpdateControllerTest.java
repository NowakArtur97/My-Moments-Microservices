package com.nowakArtur97.myMoments.postService.feature.resource;


import com.nowakArtur97.myMoments.postService.advice.ErrorResponse;
import com.nowakArtur97.myMoments.postService.feature.PostTestBuilder;
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
class InvalidPostUpdateControllerTest {

    private final String POSTS_BASE_PATH = "/api/v1/posts/{id}";

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
    void when_update_post_without_photos_should_return_error_response() {

        String postId = "postId";
        String header = "Bearer token";
        String author = "user";

        String emptyCaption = "";
        PostDTO postDTOExpected = (PostDTO) postTestBuilder.withCaption(emptyCaption).build(ObjectType.CREATE_DTO);
        String postAsString = ObjectTestMapper.asJsonString(postDTOExpected);

        MultiValueMap<String, Object> objectMultiValueMap = new LinkedMultiValueMap<>();
        objectMultiValueMap.add("post", postAsString);

        when(jwtUtil.extractUsernameFromHeader(header)).thenReturn(author);
        when(postObjectMapper.getPostDTOFromString(any(String.class), any(Flux.class))).thenReturn(Mono.just(postDTOExpected));

        Mono<ErrorResponse> errorResponseMono = webTestClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path(POSTS_BASE_PATH)
                        .build(postId))
                .body(BodyInserters.fromMultipartData(objectMultiValueMap))
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
                                    () -> assertEquals("Number of photos must be between 1 and 10.",
                                            errorResponse.getErrors().get(0),
                                            () -> "should return error response with message: " +
                                                    "'Number of photos must be between 1 and 10.'" + ", but was: "
                                                    + errorResponse.getErrors().get(0)),
                                    () -> assertEquals(1, errorResponse.getErrors().size(),
                                            () -> "should return error response with 1 message, but was: "
                                                    + errorResponse.getErrors().size()),
                                    () -> assertNotNull(errorResponse.getTimestamp(),
                                            () -> "should return error response with not null timestamp, but was: null"),
                                    () -> assertEquals(400, errorResponse.getStatus(),
                                            () -> "should return error response with 400 status, but was: "
                                                    + errorResponse.getStatus()),
                                    () -> verify(jwtUtil, times(1)).extractUsernameFromHeader(header),
                                    () -> verifyNoMoreInteractions(jwtUtil),
                                    () -> verify(postObjectMapper, times(1))
                                            .getPostDTOFromString(any(String.class), any(Flux.class)),
                                    () -> verifyNoMoreInteractions(postObjectMapper),
                                    () -> verifyNoMoreInteractions(modelMapper));
                            return true;
                        }
                ).verifyComplete();
    }

    @Test
    void when_update_post_without_photos_and_caption_should_return_error_response() {

        String postId = "postId";
        String header = "Bearer token";
        String author = "user";

        PostDTO postDTOExpected = (PostDTO) postTestBuilder.withCaption(null).build(ObjectType.CREATE_DTO);
        String postAsString = ObjectTestMapper.asJsonString(postDTOExpected);

        MultiValueMap<String, Object> objectMultiValueMap = new LinkedMultiValueMap<>();
        objectMultiValueMap.add("post", postAsString);

        when(jwtUtil.extractUsernameFromHeader(header)).thenReturn(author);
        when(postObjectMapper.getPostDTOFromString(any(String.class), any(Flux.class))).thenReturn(Mono.just(postDTOExpected));

        Mono<ErrorResponse> errorResponseMono = webTestClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path(POSTS_BASE_PATH)
                        .build(postId))
                .body(BodyInserters.fromMultipartData(objectMultiValueMap))
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
                                    () -> assertEquals("Number of photos must be between 1 and 10.",
                                            errorResponse.getErrors().get(0),
                                            () -> "should return error response with message: " +
                                                    "'Number of photos must be between 1 and 10.'" + ", but was: "
                                                    + errorResponse.getErrors().get(0)),
                                    () -> assertEquals(1, errorResponse.getErrors().size(),
                                            () -> "should return error response with 1 message, but was: "
                                                    + errorResponse.getErrors().size()),
                                    () -> assertNotNull(errorResponse.getTimestamp(),
                                            () -> "should return error response with not null timestamp, but was: null"),
                                    () -> assertEquals(400, errorResponse.getStatus(),
                                            () -> "should return error response with 400 status, but was: "
                                                    + errorResponse.getStatus()),
                                    () -> verify(jwtUtil, times(1)).extractUsernameFromHeader(header),
                                    () -> verifyNoMoreInteractions(jwtUtil),
                                    () -> verify(postObjectMapper, times(1))
                                            .getPostDTOFromString(any(String.class), any(Flux.class)),
                                    () -> verifyNoMoreInteractions(postObjectMapper),
                                    () -> verifyNoMoreInteractions(modelMapper));
                            return true;
                        }
                ).verifyComplete();
    }

    @Test
    void when_update_post_with_too_many_photos_should_return_error_response() {

        String postId = "postId";
        String header = "Bearer token";
        String author = "user";

        PostDTO postDTOExpected = (PostDTO) postTestBuilder.build(ObjectType.CREATE_DTO);
        String postAsString = ObjectTestMapper.asJsonString(postDTOExpected);

        Binary imageExpected = new Binary(BsonBinarySubType.BINARY, "image.jpg".getBytes());

        MultiValueMap<String, Object> objectMultiValueMap = new LinkedMultiValueMap<>();
        objectMultiValueMap.add("post", postAsString);
        objectMultiValueMap.add("photos", imageExpected);
        objectMultiValueMap.add("photos", imageExpected);
        objectMultiValueMap.add("photos", imageExpected);
        objectMultiValueMap.add("photos", imageExpected);
        objectMultiValueMap.add("photos", imageExpected);
        objectMultiValueMap.add("photos", imageExpected);
        objectMultiValueMap.add("photos", imageExpected);
        objectMultiValueMap.add("photos", imageExpected);
        objectMultiValueMap.add("photos", imageExpected);
        objectMultiValueMap.add("photos", imageExpected);
        objectMultiValueMap.add("photos", imageExpected);

        when(jwtUtil.extractUsernameFromHeader(header)).thenReturn(author);
        when(postObjectMapper.getPostDTOFromString(any(String.class), any(Flux.class))).thenReturn(Mono.just(postDTOExpected));

        Mono<ErrorResponse> errorResponseMono = webTestClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path(POSTS_BASE_PATH)
                        .build(postId))
                .body(BodyInserters.fromMultipartData(objectMultiValueMap))
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
                                    () -> assertEquals("Number of photos must be between 1 and 10.",
                                            errorResponse.getErrors().get(0),
                                            () -> "should return error response with message: " +
                                                    "'Number of photos must be between 1 and 10.'" + ", but was: "
                                                    + errorResponse.getErrors().get(0)),
                                    () -> assertEquals(1, errorResponse.getErrors().size(),
                                            () -> "should return error response with 1 message, but was: "
                                                    + errorResponse.getErrors().size()),
                                    () -> assertNotNull(errorResponse.getTimestamp(),
                                            () -> "should return error response with not null timestamp, but was: null"),
                                    () -> assertEquals(400, errorResponse.getStatus(),
                                            () -> "should return error response with 400 status, but was: "
                                                    + errorResponse.getStatus()),
                                    () -> verify(jwtUtil, times(1)).extractUsernameFromHeader(header),
                                    () -> verifyNoMoreInteractions(jwtUtil),
                                    () -> verify(postObjectMapper, times(1))
                                            .getPostDTOFromString(any(String.class), any(Flux.class)),
                                    () -> verifyNoMoreInteractions(postObjectMapper),
                                    () -> verifyNoMoreInteractions(modelMapper));
                            return true;
                        }
                ).verifyComplete();
    }

    @Test
    void when_update_post_with_too_long_caption_should_return_error_response() {

        String postId = "postId";
        String header = "Bearer token";
        String author = "user";

        String tooLongCaption = "caption123".repeat(100) + "!";
        Binary imageExpected = new Binary(BsonBinarySubType.BINARY, "image.jpg".getBytes());
        PostDTO postDTOExpected = (PostDTO) postTestBuilder.withCaption(tooLongCaption).withBinary(List.of(imageExpected))
                .build(ObjectType.CREATE_DTO);
        String postAsString = ObjectTestMapper.asJsonString(postDTOExpected);

        MultiValueMap<String, Object> objectMultiValueMap = new LinkedMultiValueMap<>();
        objectMultiValueMap.add("post", postAsString);
        objectMultiValueMap.add("photos",
                new ClassPathResource("example.jpg", this.getClass().getClassLoader()));

        when(jwtUtil.extractUsernameFromHeader(header)).thenReturn(author);
        when(postObjectMapper.getPostDTOFromString(any(String.class), any(Flux.class))).thenReturn(Mono.just(postDTOExpected));

        Mono<ErrorResponse> errorResponseMono = webTestClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path(POSTS_BASE_PATH)
                        .build(postId))
                .body(BodyInserters.fromMultipartData(objectMultiValueMap))
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
                                    () -> assertEquals("Caption cannot be longer than 1000.",
                                            errorResponse.getErrors().get(0),
                                            () -> "should return error response with message: " +
                                                    "'Caption cannot be longer than 1000.'" + ", but was: "
                                                    + errorResponse.getErrors().get(0)),
                                    () -> assertEquals(1, errorResponse.getErrors().size(),
                                            () -> "should return error response with 1 message, but was: "
                                                    + errorResponse.getErrors().size()),
                                    () -> assertNotNull(errorResponse.getTimestamp(),
                                            () -> "should return error response with not null timestamp, but was: null"),
                                    () -> assertEquals(400, errorResponse.getStatus(),
                                            () -> "should return error response with 400 status, but was: "
                                                    + errorResponse.getStatus()),
                                    () -> verify(jwtUtil, times(1)).extractUsernameFromHeader(header),
                                    () -> verifyNoMoreInteractions(jwtUtil),
                                    () -> verify(postObjectMapper, times(1))
                                            .getPostDTOFromString(any(String.class), any(Flux.class)),
                                    () -> verifyNoMoreInteractions(postObjectMapper),
                                    () -> verifyNoMoreInteractions(modelMapper));
                            return true;
                        }
                ).verifyComplete();
    }
}
