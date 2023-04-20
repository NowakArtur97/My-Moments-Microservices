package com.nowakArtur97.myMoments.followerService.feature.resource;

import com.nowakArtur97.myMoments.followerService.advice.ErrorResponse;
import com.nowakArtur97.myMoments.followerService.feature.UserTestBuilder;
import com.nowakArtur97.myMoments.followerService.feature.node.FollowerService;
import com.nowakArtur97.myMoments.followerService.feature.node.FollowingRelationship;
import com.nowakArtur97.myMoments.followerService.feature.node.UserNode;
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
import java.util.Collections;
import java.util.List;
import java.util.Set;

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
    private String RECOMMENDATIONS_WITH_USERNAME_PATH;

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
        RECOMMENDATIONS_WITH_USERNAME_PATH = "/api/v1/following/recommendations/{username}";
    }

    @Nested
    class FindUserTest {

        @Test
        void when_find_existing_user_following_should_return_following() {

            String header = "Bearer token";
            String username = "user";
            String followerName = "followerName";

            UserNode userNodeExpected = (UserNode) userTestBuilder.withUsername(followerName).build(ObjectType.NODE);
            FollowingRelationship followingRelationshipExpected = new FollowingRelationship(userNodeExpected);
            UserModel userModel = (UserModel) userTestBuilder.withUsername(followerName)
                    .withFollowing(Set.of(followingRelationshipExpected)).build(ObjectType.MODEL);
            UsersAcquaintancesModel usersAcquaintancesModelExpected = new UsersAcquaintancesModel(List.of(userModel));

            when(followerService.findFollowed(username)).thenReturn(Mono.just(usersAcquaintancesModelExpected));

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
                                UserModel actualUserModel = usersAcquaintancesModelExpected.getUsers().stream()
                                        .filter(user -> user.getUsername().equals(followerName))
                                        .findFirst().get();
                                assertAll(
                                        () -> assertEquals(usersAcquaintancesModelExpected.getUsers().size(),
                                                acquaintancesActual.getUsers().size(),
                                                () -> "should return following: " + usersAcquaintancesModelExpected.getUsers()
                                                        + ", but was: " + acquaintancesActual.getUsers()),
                                        () -> assertTrue(usersAcquaintancesModelExpected.getUsers().stream()
                                                        .anyMatch(user -> user.getUsername().equals(followerName)),
                                                () -> "should return follower with name: " + followerName
                                                        + ", but was: " + acquaintancesActual.getUsers()),
                                        () -> assertEquals(actualUserModel.getNumberOfFollowing(), userModel.getNumberOfFollowing(),
                                                () -> "should return follower with number of following: " + userModel.getNumberOfFollowing()
                                                        + ", but was: " + actualUserModel.getNumberOfFollowing()),
                                        () -> assertEquals(actualUserModel.getNumberOfFollowers(), userModel.getNumberOfFollowers(),
                                                () -> "should return follower with number of followers: " + userModel.getNumberOfFollowers()
                                                        + ", but was: " + actualUserModel.getNumberOfFollowers()),
                                        () -> verify(followerService, times(1)).findFollowed(username),
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

            when(followerService.findFollowed(username)).thenReturn(Mono.just(usersAcquaintancesModelExpected));

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
                                        () -> verify(followerService, times(1)).findFollowed(username),
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

    @Nested
    class RecommendUserTest {

        private final static int DEFAULT_MIN_DEGREE = 2;
        private final static int DEFAULT_MAX_DEGREE = 2;

        @Test
        void when_recommend_users_with_default_parameters_should_return_list_of_users() {

            String header = "Bearer token";
            String username = "user";
            String expectedUserName1 = "user1";
            String expectedUserName2 = "user2";

            UserModel userModel1 = (UserModel) userTestBuilder.withUsername(expectedUserName1).build(ObjectType.MODEL);
            UserModel userModel2 = (UserModel) userTestBuilder.withUsername(expectedUserName2).build(ObjectType.MODEL);
            UsersAcquaintancesModel usersAcquaintancesModelExpected = new UsersAcquaintancesModel(List.of(userModel1, userModel2));

            when(followerService.recommendUsers(username, DEFAULT_MIN_DEGREE, DEFAULT_MAX_DEGREE))
                    .thenReturn(Mono.just(usersAcquaintancesModelExpected));

            Mono<UsersAcquaintancesModel> usersAcquaintancesModelMono = webTestClient.get()
                    .uri(RECOMMENDATIONS_WITH_USERNAME_PATH, username)
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
                                                        .anyMatch(user -> user.getUsername().equals(expectedUserName1)),
                                                () -> "should return follower with name: " + expectedUserName1
                                                        + ", but was: " + acquaintancesActual.getUsers()),
                                        () -> assertTrue(usersAcquaintancesModelExpected.getUsers().stream()
                                                        .anyMatch(user -> user.getUsername().equals(expectedUserName2)),
                                                () -> "should return follower with name: " + expectedUserName2
                                                        + ", but was: " + acquaintancesActual.getUsers()),
                                        () -> verify(followerService, times(1))
                                                .recommendUsers(username, DEFAULT_MIN_DEGREE, DEFAULT_MAX_DEGREE),
                                        () -> verifyNoMoreInteractions(followerService));
                                return true;
                            }
                    ).verifyComplete();
        }

        @Test
        void when_recommend_users_with_custom_parameters_should_return_list_of_users() {

            String header = "Bearer token";
            String username = "user";
            Integer minDegree = 2;
            Integer maxDegree = 4;
            String expectedUserName1 = "user1";
            String expectedUserName2 = "user2";

            UserModel userModel1 = (UserModel) userTestBuilder.withUsername(expectedUserName1).build(ObjectType.MODEL);
            UserModel userModel2 = (UserModel) userTestBuilder.withUsername(expectedUserName2).build(ObjectType.MODEL);
            UsersAcquaintancesModel usersAcquaintancesModelExpected = new UsersAcquaintancesModel(List.of(userModel1, userModel2));

            when(followerService.recommendUsers(username, minDegree, maxDegree))
                    .thenReturn(Mono.just(usersAcquaintancesModelExpected));

            Mono<UsersAcquaintancesModel> usersAcquaintancesModelMono = webTestClient.get()
                    .uri(uriBuilder ->
                            uriBuilder
                                    .path(RECOMMENDATIONS_WITH_USERNAME_PATH.replace("{username}", username))
                                    .queryParam("minDegree", minDegree)
                                    .queryParam("maxDegree", maxDegree)
                                    .queryParam("state", "cGF5bWVudGlkPTRiMmZlMG")
                                    .build(username))
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
                                                        .anyMatch(user -> user.getUsername().equals(expectedUserName1)),
                                                () -> "should return follower with name: " + expectedUserName1
                                                        + ", but was: " + acquaintancesActual.getUsers()),
                                        () -> assertTrue(usersAcquaintancesModelExpected.getUsers().stream()
                                                        .anyMatch(user -> user.getUsername().equals(expectedUserName2)),
                                                () -> "should return follower with name: " + expectedUserName2
                                                        + ", but was: " + acquaintancesActual.getUsers()),
                                        () -> verify(followerService, times(1))
                                                .recommendUsers(username, minDegree, maxDegree),
                                        () -> verifyNoMoreInteractions(followerService));
                                return true;
                            }
                    ).verifyComplete();
        }

        @Test
        void when_recommend_users_with_custom_and_default_parameters_should_return_list_of_users() {

            String header = "Bearer token";
            String username = "user";
            Integer minDegree = 2;
            String expectedUserName1 = "user1";
            String expectedUserName2 = "user2";

            UserModel userModel1 = (UserModel) userTestBuilder.withUsername(expectedUserName1).build(ObjectType.MODEL);
            UserModel userModel2 = (UserModel) userTestBuilder.withUsername(expectedUserName2).build(ObjectType.MODEL);
            UsersAcquaintancesModel usersAcquaintancesModelExpected = new UsersAcquaintancesModel(List.of(userModel1, userModel2));

            when(followerService.recommendUsers(username, minDegree, DEFAULT_MAX_DEGREE))
                    .thenReturn(Mono.just(usersAcquaintancesModelExpected));

            Mono<UsersAcquaintancesModel> usersAcquaintancesModelMono = webTestClient.get()
                    .uri(uriBuilder ->
                            uriBuilder
                                    .path(RECOMMENDATIONS_WITH_USERNAME_PATH)
                                    .queryParam("minDegree", minDegree)
                                    .build(username))
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
                                                        .anyMatch(user -> user.getUsername().equals(expectedUserName1)),
                                                () -> "should return follower with name: " + expectedUserName1
                                                        + ", but was: " + acquaintancesActual.getUsers()),
                                        () -> assertTrue(usersAcquaintancesModelExpected.getUsers().stream()
                                                        .anyMatch(user -> user.getUsername().equals(expectedUserName2)),
                                                () -> "should return follower with name: " + expectedUserName2
                                                        + ", but was: " + acquaintancesActual.getUsers()),
                                        () -> verify(followerService, times(1))
                                                .recommendUsers(username, minDegree, DEFAULT_MAX_DEGREE),
                                        () -> verifyNoMoreInteractions(followerService));
                                return true;
                            }
                    ).verifyComplete();
        }

        @Test
        void when_recommend_users_with_default_and_custom_parameters_should_return_list_of_users() {

            String header = "Bearer token";
            String username = "user";
            Integer maxDegree = 4;
            String expectedUserName1 = "user1";
            String expectedUserName2 = "user2";

            UserModel userModel1 = (UserModel) userTestBuilder.withUsername(expectedUserName1).build(ObjectType.MODEL);
            UserModel userModel2 = (UserModel) userTestBuilder.withUsername(expectedUserName2).build(ObjectType.MODEL);
            UsersAcquaintancesModel usersAcquaintancesModelExpected = new UsersAcquaintancesModel(List.of(userModel1, userModel2));

            when(followerService.recommendUsers(username, DEFAULT_MIN_DEGREE, maxDegree))
                    .thenReturn(Mono.just(usersAcquaintancesModelExpected));

            Mono<UsersAcquaintancesModel> usersAcquaintancesModelMono = webTestClient.get()
                    .uri(uriBuilder ->
                            uriBuilder
                                    .path(RECOMMENDATIONS_WITH_USERNAME_PATH.replace("{username}", username))
                                    .queryParam("maxDegree", maxDegree)
                                    .build(username))
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
                                                        .anyMatch(user -> user.getUsername().equals(expectedUserName1)),
                                                () -> "should return follower with name: " + expectedUserName1
                                                        + ", but was: " + acquaintancesActual.getUsers()),
                                        () -> assertTrue(usersAcquaintancesModelExpected.getUsers().stream()
                                                        .anyMatch(user -> user.getUsername().equals(expectedUserName2)),
                                                () -> "should return follower with name: " + expectedUserName2
                                                        + ", but was: " + acquaintancesActual.getUsers()),
                                        () -> verify(followerService, times(1))
                                                .recommendUsers(username, DEFAULT_MIN_DEGREE, maxDegree),
                                        () -> verifyNoMoreInteractions(followerService));
                                return true;
                            }
                    ).verifyComplete();
        }


        @Test
        void when_recommend_users_but_nothing_was_found_should_return_empty_list() {

            String header = "Bearer token";
            String username = "user";

            UsersAcquaintancesModel usersAcquaintancesModelExpected = new UsersAcquaintancesModel(Collections.emptyList());

            when(followerService.recommendUsers(username, DEFAULT_MIN_DEGREE, DEFAULT_MAX_DEGREE))
                    .thenReturn(Mono.just(usersAcquaintancesModelExpected));

            Mono<UsersAcquaintancesModel> usersAcquaintancesModelMono = webTestClient.get()
                    .uri(RECOMMENDATIONS_WITH_USERNAME_PATH, username)
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
                                                () -> "should return empty list, but was: " + acquaintancesActual.getUsers()),
                                        () -> verify(followerService, times(1))
                                                .recommendUsers(username, DEFAULT_MIN_DEGREE, DEFAULT_MAX_DEGREE),
                                        () -> verifyNoMoreInteractions(followerService));
                                return true;
                            }
                    ).verifyComplete();
        }
    }
}
