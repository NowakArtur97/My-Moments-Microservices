package com.nowakArtur97.myMoments.postService.feature.resource;


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
import org.springframework.boot.web.server.LocalServerPort;
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
class PostCreateControllerTest {

    @LocalServerPort
    private int serverPort;

    private String POSTS_BASE_PATH;

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
    }

    @Test
    void when_create_valid_post_should_return_post() {

        String header = "Bearer token";
        String author = "user";

        byte[] imageBytesExpected = "image.jpg".getBytes();
        Binary imageExpected = new Binary(BsonBinarySubType.BINARY, imageBytesExpected);
        PostDTO postDTOExpected = (PostDTO) postTestBuilder.withBinary(List.of(imageExpected)).build(ObjectType.CREATE_DTO);
        String postAsString = ObjectTestMapper.asJsonString(postDTOExpected);
        PostDocument postDocumentExpected = (PostDocument) postTestBuilder.withAuthor(author)
                .withBinary(List.of(imageExpected)).build(ObjectType.DOCUMENT);
        PostModel postModelExpected = (PostModel) postTestBuilder.withAuthor(author).withBytes(List.of(imageBytesExpected))
                .build(ObjectType.MODEL);

        MultiValueMap<String, Object> objectMultiValueMap = new LinkedMultiValueMap<>();
        objectMultiValueMap.add("post", postAsString);
        objectMultiValueMap.add("photos", new ClassPathResource("example.jpg", this.getClass().getClassLoader()));

        when(jwtUtil.extractUsernameFromHeader(header)).thenReturn(author);
        when(postObjectMapper.getPostDTOFromString(any(String.class), any(Flux.class))).thenReturn(Mono.just(postDTOExpected));
        when(postService.createPost(author, postDTOExpected)).thenReturn(Mono.just(postDocumentExpected));
        when(modelMapper.map(postDocumentExpected, PostModel.class)).thenReturn(postModelExpected);

        Mono<PostModel> postModelMono = webTestClient.post()
                .uri(POSTS_BASE_PATH)
                .body(BodyInserters.fromMultipartData(objectMultiValueMap))
                .header("Authorization", header)
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
                                    () -> verify(postService, times(1)).createPost(author, postDTOExpected),
                                    () -> verifyNoMoreInteractions(postService),
                                    () -> verify(modelMapper, times(1))
                                            .map(postDocumentExpected, PostModel.class),
                                    () -> verifyNoMoreInteractions(modelMapper));
                            return true;
                        }
                ).verifyComplete();
    }

    @Test
    void when_create_valid_post_without_caption_should_return_post_with_empty_caption() {

        String header = "Bearer token";
        String author = "user";

        String emptyCaption = "";
        byte[] imageBytesExpected = "image.jpg".getBytes();
        Binary imageExpected = new Binary(BsonBinarySubType.BINARY, imageBytesExpected);
        PostDTO postDTOExpectedAsString = (PostDTO) postTestBuilder.withCaption(null).withBinary(List.of(imageExpected))
                .build(ObjectType.CREATE_DTO);
        PostDTO postDTOExpected = (PostDTO) postTestBuilder.withCaption(emptyCaption).withBinary(List.of(imageExpected))
                .build(ObjectType.CREATE_DTO);
        String postAsString = ObjectTestMapper.asJsonString(postDTOExpectedAsString);
        PostDocument postDocumentExpected = (PostDocument) postTestBuilder.withCaption(emptyCaption)
                .withAuthor(author).withBinary(List.of(imageExpected)).build(ObjectType.DOCUMENT);
        PostModel postModelExpected = (PostModel) postTestBuilder.withCaption(emptyCaption)
                .withAuthor(author).withBytes(List.of(imageBytesExpected)).build(ObjectType.MODEL);

        MultiValueMap<String, Object> objectMultiValueMap = new LinkedMultiValueMap<>();
        objectMultiValueMap.add("post", postAsString);
        objectMultiValueMap.add("photos", new ClassPathResource("example.jpg", this.getClass().getClassLoader()));

        when(jwtUtil.extractUsernameFromHeader(header)).thenReturn(author);
        when(postObjectMapper.getPostDTOFromString(any(String.class), any(Flux.class))).thenReturn(Mono.just(postDTOExpected));
        when(postService.createPost(author, postDTOExpected)).thenReturn(Mono.just(postDocumentExpected));
        when(modelMapper.map(postDocumentExpected, PostModel.class)).thenReturn(postModelExpected);

        Mono<PostModel> postModelMono = webTestClient.post()
                .uri(POSTS_BASE_PATH)
                .body(BodyInserters.fromMultipartData(objectMultiValueMap))
                .header("Authorization", header)
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
                                    () -> verify(postService, times(1)).createPost(author, postDTOExpected),
                                    () -> verifyNoMoreInteractions(postService),
                                    () -> verify(modelMapper, times(1))
                                            .map(postDocumentExpected, PostModel.class),
                                    () -> verifyNoMoreInteractions(modelMapper));
                            return true;
                        }
                ).verifyComplete();
    }
}
