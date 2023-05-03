package com.nowakArtur97.myMoments.followerService.feature.resource;

import com.nowakArtur97.myMoments.followerService.advice.ErrorResponse;
import com.nowakArtur97.myMoments.followerService.exception.ForbiddenException;
import com.nowakArtur97.myMoments.followerService.exception.ResourceNotFoundException;
import com.nowakArtur97.myMoments.followerService.feature.UserTestBuilder;
import com.nowakArtur97.myMoments.followerService.feature.node.FollowerService;
import com.nowakArtur97.myMoments.followerService.feature.node.FollowingRelationship;
import com.nowakArtur97.myMoments.followerService.feature.node.UserNode;
import com.nowakArtur97.myMoments.followerService.jwt.JwtUtil;
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

    @MockBean
    private JwtUtil jwtUtil;

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

    @Nested
    class FollowUserTest {

        @Test
        void when_follow_user_should_not_return_content() {

            String header = "Bearer token";
            String username = "user";
            String usernameToFollow = "usernameToFollow";

            Void mock = mock(Void.class);
            when(jwtUtil.extractUsernameFromHeader(header)).thenReturn(username);
            when(followerService.followUser(username, usernameToFollow)).thenReturn(Mono.just(mock));

            webTestClient.post()
                    .uri(FOLLOWING_WITH_USERNAME_PATH, usernameToFollow)
                    .header("Authorization", header)
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBody()
                    .isEmpty();

            assertAll(
                    () -> verify(jwtUtil, times(1)).extractUsernameFromHeader(header),
                    () -> verifyNoMoreInteractions(jwtUtil),
                    () -> verify(followerService, times(1)).followUser(username, usernameToFollow),
                    () -> verifyNoMoreInteractions(followerService));
        }

        @Test
        void when_follow_user_for_a_second_time_should_throw_exception() {

            String header = "Bearer token";
            String username = "user";
            String usernameToFollow = "usernameToFollow";

            when(jwtUtil.extractUsernameFromHeader(header)).thenReturn(username);
            String exceptionMessage = "User with username: '" + username + "' is already following: " + usernameToFollow + ".";
            when(followerService.followUser(username, usernameToFollow))
                    .thenReturn(Mono.error(new ForbiddenException(exceptionMessage)));

            Mono<ErrorResponse> errorResponseMono = webTestClient.post()
                    .uri(FOLLOWING_WITH_USERNAME_PATH, usernameToFollow)
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
                                assertAll(() -> assertEquals(exceptionMessage,
                                                errorResponse.getErrors().get(0),
                                                () -> "should return error response with message: " + exceptionMessage + ", but was: "
                                                        + errorResponse.getErrors().get(0)),
                                        () -> assertEquals(1, errorResponse.getErrors().size(),
                                                () -> "should return error response with 1 message, but was: "
                                                        + errorResponse.getErrors().size()),
                                        () -> assertNotNull(errorResponse.getTimestamp(),
                                                () -> "should return error response with not null timestamp, but was: null"),
                                        () -> assertEquals(HttpStatus.FORBIDDEN.value(), errorResponse.getStatus(),
                                                () -> "should return error response with " + HttpStatus.FORBIDDEN.value()
                                                        + " status, but was: " + errorResponse.getStatus()),
                                        () -> verify(jwtUtil, times(1)).extractUsernameFromHeader(header),
                                        () -> verifyNoMoreInteractions(jwtUtil),
                                        () -> verify(followerService, times(1))
                                                .followUser(username, usernameToFollow),
                                        () -> verifyNoMoreInteractions(followerService));
                                return true;
                            }
                    ).verifyComplete();
        }

        @Test
        void when_follow_user_own_account_should_throw_exception() {

            String header = "Bearer token";
            String username = "user";

            when(jwtUtil.extractUsernameFromHeader(header)).thenReturn(username);
            String exceptionMessage = "User with username: '" + username + "' cannot follow himself.";
            when(followerService.followUser(username, username)).thenThrow(new ForbiddenException(exceptionMessage));

            Mono<ErrorResponse> errorResponseMono = webTestClient.post()
                    .uri(FOLLOWING_WITH_USERNAME_PATH, username)
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
                                assertAll(() -> assertEquals(exceptionMessage,
                                                errorResponse.getErrors().get(0),
                                                () -> "should return error response with message: " + exceptionMessage + ", but was: "
                                                        + errorResponse.getErrors().get(0)),
                                        () -> assertEquals(1, errorResponse.getErrors().size(),
                                                () -> "should return error response with 1 message, but was: "
                                                        + errorResponse.getErrors().size()),
                                        () -> assertNotNull(errorResponse.getTimestamp(),
                                                () -> "should return error response with not null timestamp, but was: null"),
                                        () -> assertEquals(HttpStatus.FORBIDDEN.value(), errorResponse.getStatus(),
                                                () -> "should return error response with " + HttpStatus.FORBIDDEN.value()
                                                        + " status, but was: " + errorResponse.getStatus()),
                                        () -> verify(jwtUtil, times(1)).extractUsernameFromHeader(header),
                                        () -> verifyNoMoreInteractions(jwtUtil),
                                        () -> verify(followerService, times(1)).followUser(username, username),
                                        () -> verifyNoMoreInteractions(followerService));
                                return true;
                            }
                    ).verifyComplete();
        }

        @Test
        void when_follow_user_without_specified_username_should_return_error_response() {

            String header = "Bearer token";
            String invalidUsernameToFollow = " ";

            Mono<ErrorResponse> errorResponseMono = webTestClient.post()
                    .uri(FOLLOWING_WITH_USERNAME_PATH, invalidUsernameToFollow)
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
                                        () -> verifyNoInteractions(jwtUtil),
                                        () -> verifyNoInteractions(followerService));
                                return true;
                            }
                    ).verifyComplete();
        }
    }

    @Nested
    class UnfollowUserTest {

        @Test
        void when_unfollow_user_should_not_return_content() {

            String header = "Bearer token";
            String username = "user";
            String usernameToFollow = "usernameToFollow";

            Void mock = mock(Void.class);
            when(jwtUtil.extractUsernameFromHeader(header)).thenReturn(username);
            when(followerService.unfollowUser(username, usernameToFollow)).thenReturn(Mono.just(mock));

            webTestClient.delete()
                    .uri(FOLLOWING_WITH_USERNAME_PATH, usernameToFollow)
                    .header("Authorization", header)
                    .exchange()
                    .expectStatus()
                    .isNoContent()
                    .expectBody()
                    .isEmpty();

            assertAll(
                    () -> verify(jwtUtil, times(1)).extractUsernameFromHeader(header),
                    () -> verifyNoMoreInteractions(jwtUtil),
                    () -> verify(followerService, times(1)).unfollowUser(username, usernameToFollow),
                    () -> verifyNoMoreInteractions(followerService));
        }

        @Test
        void when_unfollow_by_not_existing_user_should_return_error_response() {

            String header = "Bearer token";
            String notExistingUser = "notExistingUser";
            String usernameToUnfollow = "usernameToFollow";

            when(jwtUtil.extractUsernameFromHeader(header)).thenReturn(notExistingUser);
            String exceptionMessage = "User with username: '" + notExistingUser + "' not found.";
            when(followerService.unfollowUser(notExistingUser, usernameToUnfollow))
                    .thenReturn(Mono.error(new ResourceNotFoundException(exceptionMessage)));

            Mono<ErrorResponse> errorResponseMono = webTestClient.delete()
                    .uri(FOLLOWING_WITH_USERNAME_PATH, usernameToUnfollow)
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
                                        () -> verify(jwtUtil, times(1)).extractUsernameFromHeader(header),
                                        () -> verifyNoMoreInteractions(jwtUtil),
                                        () -> verify(followerService, times(1))
                                                .unfollowUser(notExistingUser, usernameToUnfollow),
                                        () -> verifyNoMoreInteractions(followerService));
                                return true;
                            }
                    ).verifyComplete();
        }

        @Test
        void when_unfollow_not_existing_user_should_return_error_response() {

            String header = "Bearer token";
            String username = "user";
            String notExistingUser = "notExistingUser";

            when(jwtUtil.extractUsernameFromHeader(header)).thenReturn(username);
            String exceptionMessage = "Follower with username: '" + username + "' not found.";
            when(followerService.unfollowUser(username, notExistingUser))
                    .thenReturn(Mono.error(new ResourceNotFoundException(exceptionMessage)));

            Mono<ErrorResponse> errorResponseMono = webTestClient.delete()
                    .uri(FOLLOWING_WITH_USERNAME_PATH, notExistingUser)
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
                                        () -> verify(jwtUtil, times(1)).extractUsernameFromHeader(header),
                                        () -> verifyNoMoreInteractions(jwtUtil),
                                        () -> verify(followerService, times(1))
                                                .unfollowUser(username, notExistingUser),
                                        () -> verifyNoMoreInteractions(followerService));
                                return true;
                            }
                    ).verifyComplete();
        }

        @Test
        void when_unfollow_user_own_account_should_return_error_response() {

            String header = "Bearer token";
            String username = "user";

            when(jwtUtil.extractUsernameFromHeader(header)).thenReturn(username);
            String exceptionMessage = "User with username: '" + username + "' cannot unfollow himself.";
            when(followerService.unfollowUser(username, username)).thenReturn(Mono.error(new ForbiddenException(exceptionMessage)));

            Mono<ErrorResponse> errorResponseMono = webTestClient.delete()
                    .uri(FOLLOWING_WITH_USERNAME_PATH, username)
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
                                assertAll(() -> assertEquals(exceptionMessage,
                                                errorResponse.getErrors().get(0),
                                                () -> "should return error response with message: " + exceptionMessage + ", but was: "
                                                        + errorResponse.getErrors().get(0)),
                                        () -> assertEquals(1, errorResponse.getErrors().size(),
                                                () -> "should return error response with 1 message, but was: "
                                                        + errorResponse.getErrors().size()),
                                        () -> assertNotNull(errorResponse.getTimestamp(),
                                                () -> "should return error response with not null timestamp, but was: null"),
                                        () -> assertEquals(HttpStatus.FORBIDDEN.value(), errorResponse.getStatus(),
                                                () -> "should return error response with " + HttpStatus.FORBIDDEN.value()
                                                        + " status, but was: " + errorResponse.getStatus()),
                                        () -> verify(jwtUtil, times(1)).extractUsernameFromHeader(header),
                                        () -> verifyNoMoreInteractions(jwtUtil),
                                        () -> verify(followerService, times(1))
                                                .unfollowUser(username, username),
                                        () -> verifyNoMoreInteractions(followerService));
                                return true;
                            }
                    ).verifyComplete();
        }

        @Test
        void when_unfollow_not_followed_user_should_return_error_response() {

            String header = "Bearer token";
            String username = "user";
            String notFollowedUser = "notFollowedUser";

            when(jwtUtil.extractUsernameFromHeader(header)).thenReturn(username);
            String exceptionMessage = "User with name: '" + username + "' is not following: '" + notFollowedUser + "'.";
            when(followerService.unfollowUser(username, notFollowedUser))
                    .thenReturn(Mono.error(new ForbiddenException(exceptionMessage)));

            Mono<ErrorResponse> errorResponseMono = webTestClient.delete()
                    .uri(FOLLOWING_WITH_USERNAME_PATH, notFollowedUser)
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
                                assertAll(() -> assertEquals(exceptionMessage,
                                                errorResponse.getErrors().get(0),
                                                () -> "should return error response with message: " + exceptionMessage + ", but was: "
                                                        + errorResponse.getErrors().get(0)),
                                        () -> assertEquals(1, errorResponse.getErrors().size(),
                                                () -> "should return error response with 1 message, but was: "
                                                        + errorResponse.getErrors().size()),
                                        () -> assertNotNull(errorResponse.getTimestamp(),
                                                () -> "should return error response with not null timestamp, but was: null"),
                                        () -> assertEquals(HttpStatus.FORBIDDEN.value(), errorResponse.getStatus(),
                                                () -> "should return error response with " + HttpStatus.FORBIDDEN.value()
                                                        + " status, but was: " + errorResponse.getStatus()),
                                        () -> verify(jwtUtil, times(1)).extractUsernameFromHeader(header),
                                        () -> verifyNoMoreInteractions(jwtUtil),
                                        () -> verify(followerService, times(1))
                                                .unfollowUser(username, notFollowedUser),
                                        () -> verifyNoMoreInteractions(followerService));
                                return true;
                            }
                    ).verifyComplete();
        }

        @Test
        void when_unfollow_user_without_specified_username_should_return_error_response() {

            String header = "Bearer token";
            String invalidUsernameToUnfollow = " ";

            Mono<ErrorResponse> errorResponseMono = webTestClient.delete()
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
                                        () -> verifyNoInteractions(jwtUtil),
                                        () -> verifyNoInteractions(followerService));
                                return true;
                            }
                    ).verifyComplete();
        }
    }
}
