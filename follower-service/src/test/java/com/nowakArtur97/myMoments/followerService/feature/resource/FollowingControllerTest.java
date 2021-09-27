package com.nowakArtur97.myMoments.followerService.feature.resource;

import com.nowakArtur97.myMoments.followerService.advice.ErrorResponse;
import com.nowakArtur97.myMoments.followerService.exception.ResourceNotFoundException;
import com.nowakArtur97.myMoments.followerService.feature.UserTestBuilder;
import com.nowakArtur97.myMoments.followerService.feature.node.FollowerService;
import com.nowakArtur97.myMoments.followerService.testUtil.enums.ObjectType;
import com.nowakArtur97.myMoments.followerService.testUtil.generator.NameWithSpacesGenerator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("FollowingController_Tests")
class FollowingControllerTest {

    @LocalServerPort
    private int serverPort;

    private String FOLLOWING_WITH_USERNAME_PATH;

    @MockBean
    private FollowerService followerService;

    @Autowired
    private WebTestClient webTestClient;

    private static UserTestBuilder userTestBuilder;

    @BeforeAll
    static void setUpBuilder() {

        userTestBuilder = new UserTestBuilder();
    }

    @BeforeEach
    void setUpWebTestClient() {

        webTestClient = webTestClient
                .mutate()
                .responseTimeout(Duration.ofMillis(30000))
                .build();

        String FOLLOWING_BASE_PATH = "http://localhost:" + serverPort + "/api/v1/following";
        FOLLOWING_WITH_USERNAME_PATH = FOLLOWING_BASE_PATH + "/{username}";
    }

    @Nested
    class FindUserTest {

        @Test
        void when_find_existing_user_following_should_return_following() {

            String header = "Bearer token";
            String username = "user";
            String followerName = "followerName";

            UserModel userModel = (UserModel) userTestBuilder.withUsername(followerName).build(ObjectType.MODEL);
            UsersAcquaintancesModel usersAcquaintancesModelExpected = new UsersAcquaintancesModel(List.of(userModel));

            when(followerService.findAcquaintances(any(String.class), any(Function.class)))
                    .thenReturn(Mono.just(usersAcquaintancesModelExpected));

            Mono<UsersAcquaintancesModel> usersAcquaintancesModelMono = webTestClient.get()
                    .uri(FOLLOWING_WITH_USERNAME_PATH, username)
                    .header("Authorization", header)
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .returnResult(UsersAcquaintancesModel.class)
                    .getResponseBody()
                    .single();

            StepVerifier.create(usersAcquaintancesModelMono)
                    .thenConsumeWhile(
                            acquaintancesActual -> {
                                assertAll(
                                        () -> assertEquals(usersAcquaintancesModelExpected.getUsers().size(),
                                                acquaintancesActual.getUsers().size(),
                                                () -> "should return following: " + usersAcquaintancesModelExpected.getUsers()
                                                        + ", but was: " + acquaintancesActual.getUsers()),
                                        () -> assertTrue(usersAcquaintancesModelExpected.getUsers().stream()
                                                        .anyMatch(user -> user.getUsername().equals(followerName)),
                                                () -> "should return follower with name: " + followerName
                                                        + ", but was: " + acquaintancesActual.getUsers()),
                                        () -> verify(followerService, times(1))
                                                .findAcquaintances(any(String.class), any(Function.class)),
                                        () -> verifyNoMoreInteractions(followerService));
                                return true;
                            }
                    ).verifyComplete();
        }

        @Test
        void when_find_existing_user_following_of_user_without_following_should_return_empty_list() {

            String header = "Bearer token";
            String username = "user";

            UsersAcquaintancesModel usersAcquaintancesModelExpected = new UsersAcquaintancesModel();

            when(followerService.findAcquaintances(any(String.class), any(Function.class)))
                    .thenReturn(Mono.just(usersAcquaintancesModelExpected));

            Mono<UsersAcquaintancesModel> usersAcquaintancesModelMono = webTestClient.get()
                    .uri(FOLLOWING_WITH_USERNAME_PATH, username)
                    .header("Authorization", header)
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .returnResult(UsersAcquaintancesModel.class)
                    .getResponseBody()
                    .single();

            StepVerifier.create(usersAcquaintancesModelMono)
                    .thenConsumeWhile(
                            acquaintancesActual -> {
                                assertAll(
                                        () -> assertTrue(usersAcquaintancesModelExpected.getUsers().isEmpty(),
                                                () -> "should not return any following, but was: "
                                                        + acquaintancesActual.getUsers()),
                                        () -> verify(followerService, times(1))
                                                .findAcquaintances(any(String.class), any(Function.class)),
                                        () -> verifyNoMoreInteractions(followerService));
                                return true;
                            }
                    ).verifyComplete();
        }

        @Test
        void when_find_not_existing_user_following_should_throw_exception() {

            String header = "Bearer token";
            String username = "user";

            String exceptionMessage = "User with username: '" + username + "' not found.";

            when(followerService.findAcquaintances(any(String.class), any(Function.class)))
                    .thenReturn(Mono.error(new ResourceNotFoundException(exceptionMessage)));

            Mono<ErrorResponse> errorResponseMono = webTestClient.get()
                    .uri(FOLLOWING_WITH_USERNAME_PATH, username)
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
                                assertAll(() -> assertEquals(exceptionMessage,
                                        errorResponse.getErrors().get(0),
                                        () -> "should return error response with message: " + exceptionMessage + ", but was: "
                                                + errorResponse.getErrors().get(0)),
                                        () -> assertEquals(1, errorResponse.getErrors().size(),
                                                () -> "should return error response with 1 message, but was: "
                                                        + errorResponse.getErrors().size()),
                                        () -> assertNotNull(errorResponse.getTimestamp(),
                                                () -> "should return error response with not null timestamp, but was: null"),
                                        () -> assertEquals(HttpStatus.NOT_FOUND.value(), errorResponse.getStatus(),
                                                () -> "should return error response with " + HttpStatus.NOT_FOUND.value()
                                                        + " status, but was: " + errorResponse.getStatus()),
                                        () -> verify(followerService, times(1))
                                                .findAcquaintances(any(String.class), any(Function.class)),
                                        () -> verifyNoMoreInteractions(followerService));
                                return true;
                            }
                    ).verifyComplete();
        }

        @Test
        void when_find_user_followers_without_specified_username_should_return_error_response() {

            String header = "Bearer token";
            String invalidUsernameToUnfollow = " ";

            Mono<ErrorResponse> errorResponseMono = webTestClient.get()
                    .uri(FOLLOWING_WITH_USERNAME_PATH, invalidUsernameToUnfollow)
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
                                        () -> assertEquals("Username cannot be empty.",
                                                errorResponse.getErrors().get(0),
                                                () -> "should return error response with message: " +
                                                        "'Username cannot be empty.'" + ", but was: "
                                                        + errorResponse.getErrors().get(0)),
                                        () -> assertEquals(1, errorResponse.getErrors().size(),
                                                () -> "should return error response with 1 message, but was: "
                                                        + errorResponse.getErrors().size()),
                                        () -> assertNotNull(errorResponse.getTimestamp(),
                                                () -> "should return error response with not null timestamp, but was: null"),
                                        () -> assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus(),
                                                () -> "should return error response with " + HttpStatus.BAD_REQUEST.value()
                                                        + " status, but was: " + errorResponse.getStatus()),
                                        () -> verifyNoInteractions(followerService));
                                return true;
                            }
                    ).verifyComplete();
        }
    }
}
