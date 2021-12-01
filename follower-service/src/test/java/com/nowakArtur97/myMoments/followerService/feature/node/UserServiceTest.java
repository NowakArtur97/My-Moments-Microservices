package com.nowakArtur97.myMoments.followerService.feature.node;

import com.nowakArtur97.myMoments.followerService.feature.UserTestBuilder;
import com.nowakArtur97.myMoments.followerService.testUtil.enums.ObjectType;
import com.nowakArtur97.myMoments.followerService.testUtil.generator.NameWithSpacesGenerator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.neo4j.core.ReactiveNeo4jClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("UserService_Tests")
class UserServiceTest {

    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReactiveNeo4jClient reactiveClient;

    private static MockedStatic<UUID> mocked;

    private static UserTestBuilder userTestBuilder;

    @BeforeAll
    static void setUpBuilderAndUUID() {

        userTestBuilder = new UserTestBuilder();

        UUID uuid = UUID.randomUUID();
        mocked = mockStatic(UUID.class);
        mocked.when(UUID::randomUUID).thenReturn(uuid);
    }

    @AfterAll
    static void cleanUp() {

        if (!mocked.isClosed()) {
            mocked.close();
        }
    }

    @BeforeEach
    void setUp() {

        userService = new UserService(userRepository, reactiveClient);
    }

    @Test
    void when_save_user_should_return_user() {

        UserNode userExpected = (UserNode) userTestBuilder.build(ObjectType.NODE);

        when(userRepository.save(userExpected)).thenReturn(Mono.just(userExpected));

        Mono<UserNode> userActualMono = userService.saveUser(userExpected);

        StepVerifier.create(userActualMono)
                .thenConsumeWhile(
                        userActual -> {
                            assertUser(userExpected, userActual);
                            assertAll(
                                    () -> verify(userRepository, times(1)).save(userExpected),
                                    () -> verifyNoMoreInteractions(userRepository),
                                    () -> verifyNoInteractions(reactiveClient));
                            return true;
                        }
                ).verifyComplete();
    }

    @Test
    void when_follow_user_should_return_void() {

        String username = "user";
        String usernameToFollow = "userToFollow";

        Void expectedVoid = mock(Void.class);
        when(userRepository.follow(username, usernameToFollow)).thenReturn(Mono.just(expectedVoid));

        Mono<Void> followVoid = userService.followUser(username, usernameToFollow);

        StepVerifier.create(followVoid)
                .expectNext(expectedVoid)
                .then(() ->
                        assertAll(
                                () -> verify(userRepository, times(1)).follow(username, usernameToFollow),
                                () -> verifyNoMoreInteractions(userRepository),
                                () -> verifyNoInteractions(reactiveClient)));
    }

    @Test
    void when_unfollow_user_should_return_void() {

        String username = "user";
        String usernameToUnfollow = "userToUnfollow";

        Void expectedVoid = mock(Void.class);
        when(userRepository.unfollow(username, usernameToUnfollow)).thenReturn(Mono.just(expectedVoid));

        Mono<Void> followVoid = userService.unfollowUser(username, usernameToUnfollow);

        StepVerifier.create(followVoid)
                .expectNext(expectedVoid)
                .then(() ->
                        assertAll(
                                () -> verify(userRepository, times(1))
                                        .unfollow(username, usernameToUnfollow),
                                () -> verifyNoMoreInteractions(userRepository),
                                () -> verifyNoInteractions(reactiveClient))
                );
    }

    @Nested
    class FindUserTest {

        @Test
        void when_find_existing_user_by_username_should_return_user() {

            String username = "user";
            UserNode userExpected = (UserNode) userTestBuilder.withUsername(username).build(ObjectType.NODE);

            when(userRepository.findByUsername(username)).thenReturn(Mono.just(userExpected));

            Mono<UserNode> userActualMono = userService.findUserByUsername(username);

            StepVerifier.create(userActualMono)
                    .thenConsumeWhile(
                            userActual -> {
                                assertUser(userExpected, userActual);
                                assertAll(
                                        () -> verify(userRepository, times(1)).findByUsername(username),
                                        () -> verifyNoMoreInteractions(userRepository),
                                        () -> verifyNoInteractions(reactiveClient));
                                return true;
                            }
                    ).verifyComplete();
        }

        @Test
        void when_find_not_existing_user_by_username_should_return_empty_mono() {

            String username = "user";

            when(userRepository.findByUsername(username)).thenReturn(Mono.empty());

            Mono<UserNode> userActualMono = userService.findUserByUsername(username);

            StepVerifier.create(userActualMono)
                    .expectNextCount(0)
                    .then(() ->
                            assertAll(
                                    () -> verify(userRepository, times(1)).findByUsername(username),
                                    () -> verifyNoMoreInteractions(userRepository),
                                    () -> verifyNoInteractions(reactiveClient))
                    ).verifyComplete();
        }

        @Test
        void when_find_user_followers_should_return_followers() {

            String username = "user";
            String username2 = "user2";
            String username3 = "user3";
            UserNode userExpected = (UserNode) userTestBuilder.withUsername(username2).build(ObjectType.NODE);
            UserNode userExpected2 = (UserNode) userTestBuilder.withUsername(username3).build(ObjectType.NODE);

            when(userRepository.findFollowers(username)).thenReturn(Flux.just(userExpected, userExpected2));

            Flux<UserNode> usersActualFlux = userService.findFollowers(username);

            StepVerifier.create(usersActualFlux)
                    .expectNextMatches(userActual -> assertUser(userExpected, userActual))
                    .expectNextMatches(userActual -> assertUser(userExpected2, userActual))
                    .then(() ->
                            assertAll(
                                    () -> verify(userRepository, times(1)).findFollowers(username),
                                    () -> verifyNoMoreInteractions(userRepository),
                                    () -> verifyNoInteractions(reactiveClient))
                    ).verifyComplete();
        }

        @Test
        void when_find_user_followers_of_user_without_followers_should_return_empty_flux() {

            String username = "user";

            when(userRepository.findFollowers(username)).thenReturn(Flux.empty());

            Flux<UserNode> usersActualFlux = userService.findFollowers(username);

            StepVerifier.create(usersActualFlux)
                    .expectNextCount(0)
                    .then(() ->
                            assertAll(
                                    () -> verify(userRepository, times(1)).findFollowers(username),
                                    () -> verifyNoMoreInteractions(userRepository),
                                    () -> verifyNoInteractions(reactiveClient))
                    ).verifyComplete();
        }

        @Test
        void when_find_user_following_should_return_following() {

            String username = "user";
            String username2 = "user2";
            String username3 = "user3";
            UserNode userExpected = (UserNode) userTestBuilder.withUsername(username2).build(ObjectType.NODE);
            UserNode userExpected2 = (UserNode) userTestBuilder.withUsername(username3).build(ObjectType.NODE);

            when(userRepository.findFollowed(username)).thenReturn(Flux.just(userExpected, userExpected2));

            Flux<UserNode> usersActualFlux = userService.findFollowed(username);

            StepVerifier.create(usersActualFlux)
                    .expectNextMatches(userActual -> assertUser(userExpected, userActual))
                    .expectNextMatches(userActual -> assertUser(userExpected2, userActual))
                    .then(() ->
                            assertAll(
                                    () -> verify(userRepository, times(1)).findFollowed(username),
                                    () -> verifyNoMoreInteractions(userRepository),
                                    () -> verifyNoInteractions(reactiveClient))
                    ).verifyComplete();
        }

        @Test
        void when_find_user_followed_of_user_without_followed_should_return_empty_flux() {

            String username = "user";

            when(userRepository.findFollowed(username)).thenReturn(Flux.empty());

            Flux<UserNode> usersActualFlux = userService.findFollowed(username);

            StepVerifier.create(usersActualFlux)
                    .expectNextCount(0)
                    .then(() ->
                            assertAll(
                                    () -> verify(userRepository, times(1)).findFollowed(username),
                                    () -> verifyNoMoreInteractions(userRepository),
                                    () -> verifyNoInteractions(reactiveClient))
                    ).verifyComplete();
        }
    }

    private boolean assertUser(UserNode userExpected, UserNode userActual) {

        assertAll(() -> assertEquals(userExpected, userActual,
                () -> "should return user: " + userExpected + ", but was: " + userActual),
                () -> assertEquals(userExpected.getUsername(), userActual.getUsername(),
                        () -> "should return user with username: " + userExpected.getUsername() + ", but was: "
                                + userActual.getUsername()),
                () -> assertEquals(userExpected.getFollowing(), userActual.getFollowing(),
                        () -> "should return user with following: " + userExpected.getFollowing() + ", but was: "
                                + userActual.getFollowing()),
                () -> assertEquals(userExpected.getFollowers(), userActual.getFollowers(),
                        () -> "should return user with followers: " + userExpected.getFollowers() + ", but was: "
                                + userActual.getFollowers()));
        return true;
    }
}
