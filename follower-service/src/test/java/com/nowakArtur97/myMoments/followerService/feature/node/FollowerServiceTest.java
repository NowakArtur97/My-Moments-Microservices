package com.nowakArtur97.myMoments.followerService.feature.node;

import com.nowakArtur97.myMoments.followerService.feature.UserTestBuilder;
import com.nowakArtur97.myMoments.followerService.testUtil.enums.ObjectType;
import com.nowakArtur97.myMoments.followerService.testUtil.generator.NameWithSpacesGenerator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("FollowerService_Tests")
class FollowerServiceTest {

    private FollowerService followerService;

    @Mock
    private UserService userService;

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

        followerService = new FollowerService(userService);
    }

    @Nested
    class FollowUserTest {

        @Test
        void when_follow_not_existing_user_by_not_existing_user_should_create_users_and_follow_user() {

            String username = "user";
            String usernameToFollow = "userToFollow";
            UserNode followerExpected = userTestBuilder.withUsername(username).build(ObjectType.NODE);
            UserNode followingExpected = userTestBuilder.withUsername(usernameToFollow).build(ObjectType.NODE);
            FollowingRelationship followingRelationshipExpected = new FollowingRelationship(followingExpected);
            FollowingRelationship followedRelationshipExpected = new FollowingRelationship(followerExpected);
            UserNode followingWithFollowerExpected = userTestBuilder.withUsername(usernameToFollow)
                    .withFollowers(Set.of(followedRelationshipExpected)).build(ObjectType.NODE);
            UserNode followerWithFollowingExpected = userTestBuilder.withUsername(usernameToFollow)
                    .withFollowing(Set.of(followingRelationshipExpected)).build(ObjectType.NODE);

            when(userService.findUserByUsername(username)).thenReturn(Mono.empty());
            when(userService.findUserByUsername(usernameToFollow)).thenReturn(Mono.empty());
            when(userService.createUser(username)).thenReturn(Mono.just(followerExpected));
            when(userService.createUser(usernameToFollow)).thenReturn(Mono.just(followerExpected));
            when(userService.saveUser(followingExpected)).thenReturn(Mono.just(followingExpected));
            when(userService.saveUser(followingWithFollowerExpected)).thenReturn(Mono.just(followingWithFollowerExpected));

            Mono<Void> voidActualMono = followerService.followUser(username, usernameToFollow);

            StepVerifier.create(voidActualMono)
                    .then(() ->
                            assertAll(
                                    () -> verify(userService, times(1)).findUserByUsername(username),
                                    () -> verify(userService, times(1)).findUserByUsername(usernameToFollow),
                                    () -> verify(userService, times(1)).createUser(username),
                                    () -> verify(userService, times(1)).createUser(usernameToFollow),
                                    () -> verify(userService, times(2))
                                            .saveUser(followerWithFollowingExpected),
                                    () -> verify(userService, times(2))
                                            .saveUser(followingWithFollowerExpected),
                                    () -> verifyNoMoreInteractions(userService))
                    ).verifyComplete();
        }

        @Test
        void when_follow_existing_user_by_not_existing_user_should_create_user_and_follow_user() {

            String username = "user";
            String usernameToFollow = "userToFollow";
            UserNode followerExpected = userTestBuilder.withUsername(username).build(ObjectType.NODE);
            UserNode followingExpected = userTestBuilder.withUsername(usernameToFollow).build(ObjectType.NODE);
            FollowingRelationship followingRelationshipExpected = new FollowingRelationship(followingExpected);
            FollowingRelationship followedRelationshipExpected = new FollowingRelationship(followerExpected);
            UserNode followingWithFollowerExpected = userTestBuilder.withUsername(usernameToFollow)
                    .withFollowers(Set.of(followedRelationshipExpected)).build(ObjectType.NODE);
            UserNode followerWithFollowingExpected = userTestBuilder.withUsername(usernameToFollow)
                    .withFollowing(Set.of(followingRelationshipExpected)).build(ObjectType.NODE);

            when(userService.findUserByUsername(username)).thenReturn(Mono.empty());
            when(userService.findUserByUsername(usernameToFollow)).thenReturn(Mono.just(followingExpected));
            when(userService.createUser(username)).thenReturn(Mono.just(followerExpected));
            when(userService.saveUser(followingExpected)).thenReturn(Mono.just(followingExpected));
            when(userService.saveUser(followingWithFollowerExpected)).thenReturn(Mono.just(followingWithFollowerExpected));

            Mono<Void> voidActualMono = followerService.followUser(username, usernameToFollow);

            StepVerifier.create(voidActualMono)
                    .then(() ->
                            assertAll(
                                    () -> verify(userService, times(1)).findUserByUsername(username),
                                    () -> verify(userService, times(1)).findUserByUsername(usernameToFollow),
                                    () -> verify(userService, times(1)).createUser(username),
                                    () -> verify(userService, times(2))
                                            .saveUser(followerWithFollowingExpected),
                                    () -> verify(userService, times(2))
                                            .saveUser(followingWithFollowerExpected),
                                    () -> verifyNoMoreInteractions(userService))
                    ).verifyComplete();
        }

        @Test
        void when_follow_not_existing_user_by_existing_user_should_create_user_and_follow_user() {

            String username = "user";
            String usernameToFollow = "userToFollow";
            UserNode followerExpected = userTestBuilder.withUsername(username).build(ObjectType.NODE);
            UserNode followingExpected = userTestBuilder.withUsername(usernameToFollow).build(ObjectType.NODE);
            FollowingRelationship followingRelationshipExpected = new FollowingRelationship(followingExpected);
            FollowingRelationship followedRelationshipExpected = new FollowingRelationship(followerExpected);
            UserNode followingWithFollowerExpected = userTestBuilder.withUsername(usernameToFollow)
                    .withFollowers(Set.of(followedRelationshipExpected)).build(ObjectType.NODE);
            UserNode followerWithFollowingExpected = userTestBuilder.withUsername(usernameToFollow)
                    .withFollowing(Set.of(followingRelationshipExpected)).build(ObjectType.NODE);

            when(userService.findUserByUsername(username)).thenReturn(Mono.just(followerExpected));
            when(userService.findUserByUsername(usernameToFollow)).thenReturn(Mono.empty());
            when(userService.createUser(usernameToFollow)).thenReturn(Mono.just(followerExpected));
            when(userService.saveUser(followingExpected)).thenReturn(Mono.just(followingExpected));
            when(userService.saveUser(followingWithFollowerExpected)).thenReturn(Mono.just(followingWithFollowerExpected));

            Mono<Void> voidActualMono = followerService.followUser(username, usernameToFollow);

            StepVerifier.create(voidActualMono)
                    .then(() ->
                            assertAll(
                                    () -> verify(userService, times(1)).findUserByUsername(username),
                                    () -> verify(userService, times(1)).findUserByUsername(usernameToFollow),
                                    () -> verify(userService, times(1)).createUser(usernameToFollow),
                                    () -> verify(userService, times(2))
                                            .saveUser(followerWithFollowingExpected),
                                    () -> verify(userService, times(2))
                                            .saveUser(followingWithFollowerExpected),
                                    () -> verifyNoMoreInteractions(userService))
                    ).verifyComplete();
        }

        @Test
        void when_follow_existing_user_by_existing_user_should_follow_user() {

            String username = "user";
            String usernameToFollow = "userToFollow";
            UserNode followerExpected = userTestBuilder.withUsername(username).build(ObjectType.NODE);
            UserNode followingExpected = userTestBuilder.withUsername(usernameToFollow).build(ObjectType.NODE);
            FollowingRelationship followingRelationshipExpected = new FollowingRelationship(followingExpected);
            FollowingRelationship followedRelationshipExpected = new FollowingRelationship(followerExpected);
            UserNode followingWithFollowerExpected = userTestBuilder.withUsername(usernameToFollow)
                    .withFollowers(Set.of(followedRelationshipExpected)).build(ObjectType.NODE);
            UserNode followerWithFollowingExpected = userTestBuilder.withUsername(usernameToFollow)
                    .withFollowing(Set.of(followingRelationshipExpected)).build(ObjectType.NODE);

            when(userService.findUserByUsername(username)).thenReturn(Mono.just(followerExpected));
            when(userService.findUserByUsername(usernameToFollow)).thenReturn(Mono.just(followingExpected));
            when(userService.saveUser(followingExpected)).thenReturn(Mono.just(followingExpected));
            when(userService.saveUser(followingWithFollowerExpected)).thenReturn(Mono.just(followingWithFollowerExpected));

            Mono<Void> voidActualMono = followerService.followUser(username, usernameToFollow);

            StepVerifier.create(voidActualMono)
                    .then(() ->
                            assertAll(
                                    () -> verify(userService, times(1)).findUserByUsername(username),
                                    () -> verify(userService, times(1)).findUserByUsername(usernameToFollow),
                                    () -> verify(userService, times(2))
                                            .saveUser(followerWithFollowingExpected),
                                    () -> verify(userService, times(2))
                                            .saveUser(followingWithFollowerExpected),
                                    () -> verifyNoMoreInteractions(userService))
                    ).verifyComplete();
        }

        @Test
        void when_follow_user_for_a_second_time_should_throw_exception() {

            String username = "user";
            String usernameToFollow = "userToFollow";
            UserNode followingWithFollowerExpected = userTestBuilder.withUsername(username).build(ObjectType.NODE);
            UserNode followerWithFollowingExpected = userTestBuilder.withUsername(usernameToFollow).build(ObjectType.NODE);
            FollowingRelationship followingRelationshipExpected = new FollowingRelationship(followingWithFollowerExpected);
            FollowingRelationship followedRelationshipExpected = new FollowingRelationship(followerWithFollowingExpected);
            followingWithFollowerExpected.getFollowing().add(followingRelationshipExpected);
            followerWithFollowingExpected.getFollowing().add(followedRelationshipExpected);

            when(userService.findUserByUsername(username)).thenReturn(Mono.just(followingWithFollowerExpected));
            when(userService.findUserByUsername(usernameToFollow)).thenReturn(Mono.just(followerWithFollowingExpected));

            Mono<Void> voidActualMono = followerService.followUser(username, usernameToFollow);

            StepVerifier.create(voidActualMono)
                    .then(() ->
                            assertAll(
                                    () -> verify(userService, times(1)).findUserByUsername(username),
                                    () -> verify(userService, times(1)).findUserByUsername(usernameToFollow),
                                    () -> verifyNoMoreInteractions(userService))
                    ).verifyErrorMessage("User with username: '" + username + "' is already following: " + usernameToFollow + ".");
        }

        @Test
        void when_follow_user_own_account_should_throw_exception() {

            String username = "user";

            Mono<Void> voidActualMono = followerService.followUser(username, username);

            StepVerifier.create(voidActualMono)
                    .then(() ->
                            assertAll(
                                    () -> verifyNoInteractions(userService))
                    ).verifyErrorMessage("User with username: '" + username + "' cannot follow himself.");
        }
    }

    @Nested
    class UnfollowUserTest {

        @Test
        void when_unfollow_user_should_unfollow_user() {

            String username = "user";
            String usernameToUnfollow = "userToUnfollow";

            UserNode followingWithFollowerExpected = userTestBuilder.withUsername(username).build(ObjectType.NODE);
            UserNode followerWithFollowingExpected = userTestBuilder.withUsername(usernameToUnfollow).build(ObjectType.NODE);
            FollowingRelationship followingRelationshipExpected = new FollowingRelationship(followingWithFollowerExpected);
            FollowingRelationship followedRelationshipExpected = new FollowingRelationship(followerWithFollowingExpected);
            followingWithFollowerExpected.getFollowing().add(followingRelationshipExpected);
            followerWithFollowingExpected.getFollowing().add(followedRelationshipExpected);
            UserNode followerExpected = userTestBuilder.withUsername(username).build(ObjectType.NODE);
            UserNode followingExpected = userTestBuilder.withUsername(usernameToUnfollow).build(ObjectType.NODE);

            when(userService.findUserByUsername(username)).thenReturn(Mono.just(followingWithFollowerExpected));
            when(userService.findUserByUsername(usernameToUnfollow)).thenReturn(Mono.just(followerWithFollowingExpected));
            when(userService.saveUser(followerExpected)).thenReturn(Mono.just(followerExpected));
            when(userService.saveUser(followingExpected)).thenReturn(Mono.just(followingExpected));

            Mono<Void> voidActualMono = followerService.unfollowUser(username, usernameToUnfollow);

            StepVerifier.create(voidActualMono)
                    .then(() ->
                            assertAll(
                                    () -> verify(userService, times(1)).findUserByUsername(username),
                                    () -> verify(userService, times(1)).findUserByUsername(usernameToUnfollow),
                                    () -> verify(userService, times(2)).saveUser(followerExpected),
                                    () -> verify(userService, times(2)).saveUser(followingExpected),
                                    () -> verifyNoMoreInteractions(userService))
                    ).verifyComplete();
        }

        @Test
        void when_unfollow_by_not_existing_user_should_throw_exception() {

            String username = "notExistingUser";
            String usernameToUnfollow = "userToUnfollow";
            UserNode followerWithFollowingExpected = userTestBuilder.withUsername(usernameToUnfollow).build(ObjectType.NODE);
            FollowingRelationship followedRelationshipExpected = new FollowingRelationship(followerWithFollowingExpected);
            followerWithFollowingExpected.getFollowing().add(followedRelationshipExpected);

            when(userService.findUserByUsername(username)).thenReturn(Mono.empty());
            when(userService.findUserByUsername(usernameToUnfollow)).thenReturn(Mono.just(followerWithFollowingExpected));

            Mono<Void> voidActualMono = followerService.unfollowUser(username, usernameToUnfollow);

            StepVerifier.create(voidActualMono)
                    .then(() ->
                            assertAll(
                                    () -> verify(userService, times(1)).findUserByUsername(username),
                                    () -> verify(userService, times(1)).findUserByUsername(usernameToUnfollow),
                                    () -> verifyNoMoreInteractions(userService))
                    ).verifyErrorMessage("User with username: '" + username + "' not found.");
        }

        @Test
        void when_unfollow_not_existing_user_should_throw_exception() {

            String username = "user";
            String usernameToUnfollow = "notExistingUserToUnfollow";
            UserNode followingWithFollowerExpected = userTestBuilder.withUsername(username).build(ObjectType.NODE);
            FollowingRelationship followingRelationshipExpected = new FollowingRelationship(followingWithFollowerExpected);
            followingWithFollowerExpected.getFollowing().add(followingRelationshipExpected);

            when(userService.findUserByUsername(username)).thenReturn(Mono.just(followingWithFollowerExpected));
            when(userService.findUserByUsername(usernameToUnfollow)).thenReturn(Mono.empty());

            Mono<Void> voidActualMono = followerService.unfollowUser(username, usernameToUnfollow);

            StepVerifier.create(voidActualMono)
                    .then(() ->
                            assertAll(
                                    () -> verify(userService, times(1)).findUserByUsername(username),
                                    () -> verify(userService, times(1)).findUserByUsername(usernameToUnfollow),
                                    () -> verifyNoMoreInteractions(userService))
                    ).verifyErrorMessage("Follower with username: '" + usernameToUnfollow + "' not found.");
        }

        @Test
        void when_unfollow_user_own_account_should_throw_exception() {

            String username = "user";

            Mono<Void> voidActualMono = followerService.unfollowUser(username, username);

            StepVerifier.create(voidActualMono)
                    .then(() ->
                            assertAll(
                                    () -> verifyNoInteractions(userService))
                    ).verifyErrorMessage("User with username: '" + username + "' cannot unfollow himself.");
        }
    }
}
