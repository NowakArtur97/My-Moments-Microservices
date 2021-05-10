package com.nowakArtur97.myMoments.postService.feature.post;


import com.nowakArtur97.myMoments.postService.common.model.ErrorResponse;
import com.nowakArtur97.myMoments.postService.common.util.JwtUtil;
import com.nowakArtur97.myMoments.postService.testUtil.enums.ObjectType;
import com.nowakArtur97.myMoments.postService.testUtil.generator.NameWithSpacesGenerator;
import com.nowakArtur97.myMoments.postService.testUtil.mapper.ObjectTestMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("PostController_Tests")
class PostCreateControllerTest {

    @LocalServerPort
    private int serverPort;

    private String POSTS_BASE_PATH;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private JwtUtil jwtUtil;

    private String token;

    private static PostTestBuilder postTestBuilder;

    @BeforeAll
    static void setUpBuilders() {

        postTestBuilder = new PostTestBuilder();
    }

    @BeforeEach
    void setUp() {

        webTestClient = webTestClient
                .mutate()
                .responseTimeout(Duration.ofMillis(30000))
                .build();

        token = jwtUtil.generateToken(new User("user", "user",
                List.of(new SimpleGrantedAuthority("USER_ROLE"))));

        POSTS_BASE_PATH = "http://localhost:" + serverPort + "/api/v1/posts";
    }

    @Test
    @SneakyThrows
    void when_create_valid_post_should_return_post() {

        PostDTO postDTOExpected = (PostDTO) postTestBuilder.build(ObjectType.CREATE_DTO);
        String postAsString = ObjectTestMapper.asJsonString(postDTOExpected);
        PostModel postModelExpected = (PostModel) postTestBuilder.withAuthor("user").build(ObjectType.MODEL);

        MultiValueMap<String, Object> objectMultiValueMap = new LinkedMultiValueMap<>();
        objectMultiValueMap.add("post", postAsString);
        objectMultiValueMap.add("photos",
                new ClassPathResource("example.jpg", this.getClass().getClassLoader()));

        Mono<PostModel> postModelMono = webTestClient.post()
                .uri(POSTS_BASE_PATH)
                .body(BodyInserters.fromMultipartData(objectMultiValueMap))
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus()
                .isCreated()
                .returnResult(PostModel.class)
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
                                    () -> assertEquals(postModelExpected.getAuthor(), postModelActual.getAuthor(),
                                            () -> "should return post with author: " + postModelExpected.getAuthor()
                                                    + ", but was: " + postModelActual.getAuthor()));
                            return true;
                        }
                ).verifyComplete();
    }

    @Test
    void when_create_valid_post_without_caption_should_return_post_with_empty_caption() {

        String emptyCaption = "";
        PostDTO postDTOExpected = (PostDTO) postTestBuilder.withCaption(null).build(ObjectType.CREATE_DTO);
        String postAsString = ObjectTestMapper.asJsonString(postDTOExpected);
        PostModel postModelExpected = (PostModel) postTestBuilder.withAuthor("user").build(ObjectType.MODEL);

        MultiValueMap<String, Object> objectMultiValueMap = new LinkedMultiValueMap<>();
        objectMultiValueMap.add("post", postAsString);
        objectMultiValueMap.add("photos",
                new ClassPathResource("example.jpg", this.getClass().getClassLoader()));

        Mono<PostModel> postModelMono = webTestClient.post()
                .uri(POSTS_BASE_PATH)
                .body(BodyInserters.fromMultipartData(objectMultiValueMap))
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus()
                .isCreated()
                .returnResult(PostModel.class)
                .getResponseBody()
                .single();

        StepVerifier.create(postModelMono)
                .thenConsumeWhile(
                        postModelActual -> {
                            assertAll(
                                    () -> assertNotNull(postModelActual.getId(),
                                            () -> "should return post with not null id, but was: null"),
                                    () -> assertEquals(emptyCaption, postModelActual.getCaption(),
                                            () -> "should return post with caption: " + emptyCaption + ", but was: "
                                                    + postModelActual.getCaption()),
                                    () -> assertEquals(postModelExpected.getAuthor(), postModelActual.getAuthor(),
                                            () -> "should return post with author: " + postModelExpected.getAuthor()
                                                    + ", but was: " + postModelActual.getAuthor()));
                            return true;
                        }
                ).verifyComplete();
    }

    @Test
    void when_create_post_without_photos_should_return_error_response() {

        String emptyCaption = "";
        PostDTO postDTOExpected = (PostDTO) postTestBuilder.withCaption(emptyCaption).build(ObjectType.CREATE_DTO);
        String postAsString = ObjectTestMapper.asJsonString(postDTOExpected);

        MultiValueMap<String, Object> objectMultiValueMap = new LinkedMultiValueMap<>();
        objectMultiValueMap.add("post", postAsString);

        Mono<ErrorResponse> errorResponseMono = webTestClient.post()
                .uri(POSTS_BASE_PATH)
                .body(BodyInserters.fromMultipartData(objectMultiValueMap))
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
                                                    + errorResponse.getStatus()));
                            return true;
                        }
                ).verifyComplete();
    }

    @Test
    void when_create_post_without_photos_and_caption_should_return_error_response() {

        PostDTO postDTOExpected = (PostDTO) postTestBuilder.withCaption(null).build(ObjectType.CREATE_DTO);
        String postAsString = ObjectTestMapper.asJsonString(postDTOExpected);

        MultiValueMap<String, Object> objectMultiValueMap = new LinkedMultiValueMap<>();
        objectMultiValueMap.add("post", postAsString);

        Mono<ErrorResponse> errorResponseMono = webTestClient.post()
                .uri(POSTS_BASE_PATH)
                .body(BodyInserters.fromMultipartData(objectMultiValueMap))
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
                                                    + errorResponse.getStatus()));
                            return true;
                        }
                ).verifyComplete();
    }

    @Test
    void when_create_post_with_too_many_photos_should_return_error_response() {

        PostDTO postDTOExpected = (PostDTO) postTestBuilder.build(ObjectType.CREATE_DTO);
        String postAsString = ObjectTestMapper.asJsonString(postDTOExpected);

        ClassPathResource resource = new ClassPathResource("example.jpg", this.getClass().getClassLoader());

        MultiValueMap<String, Object> objectMultiValueMap = new LinkedMultiValueMap<>();
        objectMultiValueMap.add("post", postAsString);
        objectMultiValueMap.add("photos", resource);
        objectMultiValueMap.add("photos", resource);
        objectMultiValueMap.add("photos", resource);
        objectMultiValueMap.add("photos", resource);
        objectMultiValueMap.add("photos", resource);
        objectMultiValueMap.add("photos", resource);
        objectMultiValueMap.add("photos", resource);
        objectMultiValueMap.add("photos", resource);
        objectMultiValueMap.add("photos", resource);
        objectMultiValueMap.add("photos", resource);
        objectMultiValueMap.add("photos", resource);

        Mono<ErrorResponse> errorResponseMono = webTestClient.post()
                .uri(POSTS_BASE_PATH)
                .body(BodyInserters.fromMultipartData(objectMultiValueMap))
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
                                                    + errorResponse.getStatus()));
                            return true;
                        }
                ).verifyComplete();
    }

    @Test
    void when_create_post_with_too_long_caption_should_return_error_response() {

        String tooLongCaption = "caption123".repeat(100) + "!";
        PostDTO postDTOExpected = (PostDTO) postTestBuilder.withCaption(tooLongCaption).build(ObjectType.CREATE_DTO);
        String postAsString = ObjectTestMapper.asJsonString(postDTOExpected);

        MultiValueMap<String, Object> objectMultiValueMap = new LinkedMultiValueMap<>();
        objectMultiValueMap.add("post", postAsString);
        objectMultiValueMap.add("photos",
                new ClassPathResource("example.jpg", this.getClass().getClassLoader()));

        Mono<ErrorResponse> errorResponseMono = webTestClient.post()
                .uri(POSTS_BASE_PATH)
                .body(BodyInserters.fromMultipartData(objectMultiValueMap))
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
                                                    + errorResponse.getStatus()));
                            return true;
                        }
                ).verifyComplete();
    }

    @Test
    void when_create_valid_post_without_token_should_return_error_response() {

        PostDTO postDTOExpected = (PostDTO) postTestBuilder.build(ObjectType.CREATE_DTO);
        String postAsString = ObjectTestMapper.asJsonString(postDTOExpected);

        MultiValueMap<String, Object> objectMultiValueMap = new LinkedMultiValueMap<>();
        objectMultiValueMap.add("post", postAsString);
        objectMultiValueMap.add("photos",
                new ClassPathResource("example.jpg", this.getClass().getClassLoader()));

        Mono<ErrorResponse> errorResponseMono = webTestClient.post()
                .uri(POSTS_BASE_PATH)
                .body(BodyInserters.fromMultipartData(objectMultiValueMap))
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
                                                    + errorResponse.getStatus()));
                            return true;
                        }
                ).verifyComplete();
    }
}
