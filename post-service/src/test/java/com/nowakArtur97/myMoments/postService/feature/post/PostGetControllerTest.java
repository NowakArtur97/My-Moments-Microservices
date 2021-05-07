package com.nowakArtur97.myMoments.postService.feature.post;

import com.nowakArtur97.myMoments.postService.common.model.ErrorResponse;
import com.nowakArtur97.myMoments.postService.common.util.JwtUtil;
import com.nowakArtur97.myMoments.postService.testUtil.enums.ObjectType;
import com.nowakArtur97.myMoments.postService.generator.NameWithSpacesGenerator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
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
@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("PostController_Tests")
class PostGetControllerTest {

    @LocalServerPort
    private int serverPort;

    @Value("${my-moments.default-user-role:USER_ROLE}")
    private String defaultUserRole;

    private String POSTS_BASE_PATH;

    @MockBean
    private PostService postService;

    @MockBean
    private PostObjectMapper postObjectMapper;

    @MockBean
    private ModelMapper modelMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private WebTestClient webTestClient;

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
                List.of(new SimpleGrantedAuthority(defaultUserRole))));

        POSTS_BASE_PATH = "http://localhost:" + serverPort + "/api/v1/posts/{id}";
    }

    @Test
    void when_find_existing_post_should_return_post() {

        String postId = "some generated id";

        PostDocument postDocumentExpected = (PostDocument) postTestBuilder.withId(postId).build(ObjectType.DOCUMENT);
        PostModel postModelExpected = (PostModel) postTestBuilder.withId(postId).build(ObjectType.MODEL);

        when(postService.findPostById(postId)).thenReturn(Mono.just(postDocumentExpected));
        when(modelMapper.map(postDocumentExpected, PostModel.class)).thenReturn(postModelExpected);

        Mono<PostModel> postModelMono = webTestClient.get()
                .uri(POSTS_BASE_PATH, postId)
                .header("Authorization", "Bearer " + token)
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
                                    () -> assertNotNull(postModelActual.getId(),
                                            () -> "should return post with not null id, but was: null"),
                                    () -> assertEquals(postModelExpected.getCaption(), postModelActual.getCaption(),
                                            () -> "should return post with caption: " + postModelExpected.getCaption()
                                                    + ", but was: " + postModelActual.getCaption()),
                                    () -> assertEquals(postModelExpected.getAuthor(), postModelActual.getAuthor(),
                                            () -> "should return post with author: " + postModelExpected.getAuthor()
                                                    + ", but was: " + postModelActual.getAuthor()),
                                    () -> verify(postService, times(1)).findPostById(postId),
                                    () -> verifyNoMoreInteractions(postService),
                                    () -> verify(modelMapper, times(1))
                                            .map(postDocumentExpected, PostModel.class),
                                    () -> verifyNoMoreInteractions(modelMapper),
                                    () -> verifyNoInteractions(postObjectMapper));
                            return true;
                        }
                ).verifyComplete();
    }

    @Test
    void when_find_not_existing_post_should_return_err_rresponse() {

        String postId = "some generated id";

        when(postService.findPostById(postId)).thenReturn(Mono.empty());

        Mono<ErrorResponse> errorResponseMono = webTestClient.get()
                .uri(POSTS_BASE_PATH, postId)
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
                                    () -> verify(postService, times(1)).findPostById(postId),
                                    () -> verifyNoMoreInteractions(postService),
                                    () -> verifyNoInteractions(modelMapper),
                                    () -> verifyNoInteractions(postObjectMapper));
                            return true;
                        }
                ).verifyComplete();
    }
}
