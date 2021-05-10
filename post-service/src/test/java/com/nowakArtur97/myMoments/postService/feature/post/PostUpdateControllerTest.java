package com.nowakArtur97.myMoments.postService.feature.post;


import com.nowakArtur97.myMoments.postService.common.model.ErrorResponse;
import com.nowakArtur97.myMoments.postService.common.util.JwtUtil;
import com.nowakArtur97.myMoments.postService.testUtil.enums.ObjectType;
import com.nowakArtur97.myMoments.postService.testUtil.generator.NameWithSpacesGenerator;
import com.nowakArtur97.myMoments.postService.testUtil.mapper.ObjectTestMapper;
import lombok.SneakyThrows;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
class PostUpdateControllerTest {

    private final String POSTS_BASE_PATH = "/api/v1/posts/{id}";

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PostRepository postRepository;

    private String userToken;
    private String adminToken;
    private PostDocument postDocument;

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

        postDocument = (PostDocument) postTestBuilder.withCaption("new").withAuthor("user")
                .withBinary(List.of(new Binary(BsonBinarySubType.BINARY, "image.jpg".getBytes())))
                .build(ObjectType.DOCUMENT);

        postDocument = postRepository.save(postDocument).block();

        userToken = jwtUtil.generateToken(new User("user", "user",
                List.of(new SimpleGrantedAuthority("USER_ROLE"))));
        adminToken = jwtUtil.generateToken(new User("admin", "admin",
                List.of(new SimpleGrantedAuthority("ADMIN_ROLE"))));
    }

    @Test
    @SneakyThrows
    void when_update_valid_post_should_return_post() {

        PostDTO postDTOExpected = (PostDTO) postTestBuilder.build(ObjectType.CREATE_DTO);
        String postAsString = ObjectTestMapper.asJsonString(postDTOExpected);
        PostModel postModelExpected = (PostModel) postTestBuilder.withId(postDocument.getId()).withAuthor("user")
                .build(ObjectType.MODEL);

        MultiValueMap<String, Object> objectMultiValueMap = new LinkedMultiValueMap<>();
        objectMultiValueMap.add("post", postAsString);
        objectMultiValueMap.add("photos",
                new ClassPathResource("example.jpg", this.getClass().getClassLoader()));

        Mono<PostModel> postModelMono = webTestClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path(POSTS_BASE_PATH)
                        .build(postDocument.getId()))
                .body(BodyInserters.fromMultipartData(objectMultiValueMap))
                .header("Authorization", "Bearer " + userToken)
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
                                    () -> assertEquals(postModelExpected.getAuthor(), postModelActual.getAuthor(),
                                            () -> "should return post with author: " + postModelExpected.getAuthor()
                                                    + ", but was: " + postModelActual.getAuthor()));
                            return true;
                        }
                ).verifyComplete();
    }

    @Test
    void when_update_valid_post_without_caption_should_return_post_with_empty_caption() {

        String emptyCaption = "";
        PostDTO postDTOExpected = (PostDTO) postTestBuilder.withCaption(null).build(ObjectType.CREATE_DTO);
        String postAsString = ObjectTestMapper.asJsonString(postDTOExpected);
        PostModel postModelExpected = (PostModel) postTestBuilder.withId(postDocument.getId()).withAuthor("user")
                .build(ObjectType.MODEL);

        MultiValueMap<String, Object> objectMultiValueMap = new LinkedMultiValueMap<>();
        objectMultiValueMap.add("post", postAsString);
        objectMultiValueMap.add("photos",
                new ClassPathResource("example.jpg", this.getClass().getClassLoader()));

        Mono<PostModel> postModelMono = webTestClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path(POSTS_BASE_PATH)
                        .build(postDocument.getId()))
                .body(BodyInserters.fromMultipartData(objectMultiValueMap))
                .header("Authorization", "Bearer " + userToken)
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
                                    () -> assertEquals(postModelExpected.getAuthor(), postModelActual.getAuthor(),
                                            () -> "should return post with author: " + postModelExpected.getAuthor()
                                                    + ", but was: " + postModelActual.getAuthor()));
                            return true;
                        }
                ).verifyComplete();
    }

    @Test
    void when_update_post_without_photos_should_return_error_response() {

        String emptyCaption = "";
        PostDTO postDTOExpected = (PostDTO) postTestBuilder.withCaption(emptyCaption).build(ObjectType.CREATE_DTO);
        String postAsString = ObjectTestMapper.asJsonString(postDTOExpected);

        MultiValueMap<String, Object> objectMultiValueMap = new LinkedMultiValueMap<>();
        objectMultiValueMap.add("post", postAsString);

        Mono<ErrorResponse> errorResponseMono = webTestClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path(POSTS_BASE_PATH)
                        .build(postDocument.getId()))
                .body(BodyInserters.fromMultipartData(objectMultiValueMap))
                .header("Authorization", "Bearer " + userToken)
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
    void when_update_post_without_photos_and_caption_should_return_error_response() {

        PostDTO postDTOExpected = (PostDTO) postTestBuilder.withCaption(null).build(ObjectType.CREATE_DTO);
        String postAsString = ObjectTestMapper.asJsonString(postDTOExpected);

        MultiValueMap<String, Object> objectMultiValueMap = new LinkedMultiValueMap<>();
        objectMultiValueMap.add("post", postAsString);

        Mono<ErrorResponse> errorResponseMono = webTestClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path(POSTS_BASE_PATH)
                        .build(postDocument.getId()))
                .body(BodyInserters.fromMultipartData(objectMultiValueMap))
                .header("Authorization", "Bearer " + userToken)
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
    void when_update_post_with_too_many_photos_should_return_error_response() {

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

        Mono<ErrorResponse> errorResponseMono = webTestClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path(POSTS_BASE_PATH)
                        .build(postDocument.getId()))
                .body(BodyInserters.fromMultipartData(objectMultiValueMap))
                .header("Authorization", "Bearer " + userToken)
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
    void when_update_post_with_too_long_caption_should_return_error_response() {

        String tooLongCaption = "caption123".repeat(100) + "!";
        PostDTO postDTOExpected = (PostDTO) postTestBuilder.withCaption(tooLongCaption).build(ObjectType.CREATE_DTO);
        String postAsString = ObjectTestMapper.asJsonString(postDTOExpected);

        MultiValueMap<String, Object> objectMultiValueMap = new LinkedMultiValueMap<>();
        objectMultiValueMap.add("post", postAsString);
        objectMultiValueMap.add("photos",
                new ClassPathResource("example.jpg", this.getClass().getClassLoader()));

        Mono<ErrorResponse> errorResponseMono = webTestClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path(POSTS_BASE_PATH)
                        .build(postDocument.getId()))
                .body(BodyInserters.fromMultipartData(objectMultiValueMap))
                .header("Authorization", "Bearer " + userToken)
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
    void when_update_user_post_without_token_should_return_error_response() {

        PostDTO postDTOExpected = (PostDTO) postTestBuilder.withCaption(null).build(ObjectType.CREATE_DTO);
        String postAsString = ObjectTestMapper.asJsonString(postDTOExpected);

        MultiValueMap<String, Object> objectMultiValueMap = new LinkedMultiValueMap<>();
        objectMultiValueMap.add("post", postAsString);
        objectMultiValueMap.add("photos",
                new ClassPathResource("example.jpg", this.getClass().getClassLoader()));

        Mono<ErrorResponse> errorResponseMono = webTestClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path(POSTS_BASE_PATH)
                        .build(postDocument.getId()))
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

    @Test
    void when_update_not_existing_user_post_should_return_error_response() {

        String notExistingUsername = "iAmNotExist";
        PostDTO postDTOExpected = (PostDTO) postTestBuilder.withCaption(null).build(ObjectType.CREATE_DTO);
        String postAsString = ObjectTestMapper.asJsonString(postDTOExpected);

        MultiValueMap<String, Object> objectMultiValueMap = new LinkedMultiValueMap<>();
        objectMultiValueMap.add("post", postAsString);
        objectMultiValueMap.add("photos",
                new ClassPathResource("example.jpg", this.getClass().getClassLoader()));

        String token = jwtUtil.generateToken(new User(notExistingUsername, notExistingUsername,
                List.of(new SimpleGrantedAuthority("USER_ROLE"))));

        Mono<ErrorResponse> errorResponseMono = webTestClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path(POSTS_BASE_PATH)
                        .build(postDocument.getId()))
                .body(BodyInserters.fromMultipartData(objectMultiValueMap))
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
                                    () -> assertEquals("User with name/email: '" + notExistingUsername
                                                    + "' not found.", errorResponse.getErrors().get(0),
                                            () -> "should return error response with message: " +
                                                    "'User with name/email: '" + notExistingUsername + "' not found.'"
                                                    + ", but was: " + errorResponse.getErrors().get(0)),
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

    @Test
    void when_update_not_existing_post_should_return_error_response() {

        String notExistingPostId = "not existing id";
        PostDTO postDTOExpected = (PostDTO) postTestBuilder.withCaption(null).build(ObjectType.CREATE_DTO);
        String postAsString = ObjectTestMapper.asJsonString(postDTOExpected);

        MultiValueMap<String, Object> objectMultiValueMap = new LinkedMultiValueMap<>();
        objectMultiValueMap.add("post", postAsString);
        objectMultiValueMap.add("photos",
                new ClassPathResource("example.jpg", this.getClass().getClassLoader()));

        Mono<ErrorResponse> errorResponseMono = webTestClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path(POSTS_BASE_PATH)
                        .build(notExistingPostId))
                .body(BodyInserters.fromMultipartData(objectMultiValueMap))
                .header("Authorization", "Bearer " + adminToken)
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
                                                    + errorResponse.getStatus()));
                            return true;
                        }
                ).verifyComplete();
    }

    @Test
    void when_update_other_user_post_should_return_error_response() {

        PostDTO postDTOExpected = (PostDTO) postTestBuilder.withCaption(null).build(ObjectType.CREATE_DTO);
        String postAsString = ObjectTestMapper.asJsonString(postDTOExpected);

        MultiValueMap<String, Object> objectMultiValueMap = new LinkedMultiValueMap<>();
        objectMultiValueMap.add("post", postAsString);
        objectMultiValueMap.add("photos",
                new ClassPathResource("example.jpg", this.getClass().getClassLoader()));

        Mono<ErrorResponse> errorResponseMono = webTestClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path(POSTS_BASE_PATH)
                        .build(postDocument.getId()))
                .body(BodyInserters.fromMultipartData(objectMultiValueMap))
                .header("Authorization", "Bearer " + adminToken)
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
                                                    + errorResponse.getStatus()));
                            return true;
                        }
                ).verifyComplete();
    }
}
