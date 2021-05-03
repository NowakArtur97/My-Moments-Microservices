package com.nowakArtur97.myMoments.postService.feature.post;


import com.nowakArtur97.myMoments.postService.common.util.JwtUtil;
import com.nowakArtur97.myMoments.postService.enums.ObjectType;
import com.nowakArtur97.myMoments.postService.generator.NameWithSpacesGenerator;
import com.nowakArtur97.myMoments.postService.testUtil.mapper.ObjectTestMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("PostController_Tests")
class PostCreateControllerTest {

    @LocalServerPort
    private int serverPort;

    private String POSTS_BASE_PATH;

    @Value("${my-moments.default-user-role:USER_ROLE}")
    private String defaultUserRole;

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
                List.of(new SimpleGrantedAuthority(defaultUserRole))));

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
                                            () -> "should return post with caption: " + postModelExpected.getCaption() + ", but was: "
                                                    + postModelActual.getCaption()),
                                    () -> assertEquals(postModelExpected.getAuthor(), postModelActual.getAuthor(),
                                            () -> "should return post with author: " + postModelExpected.getAuthor() + ", but was: "
                                                    + postModelActual.getAuthor()));
                            return true;
                        }
                ).verifyComplete();
    }
}
