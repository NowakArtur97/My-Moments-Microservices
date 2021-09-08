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
                                    () -> verify(userService, times(1))
                                            .saveUser(followerWithFollowingExpected),
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
            when(userService.createUser(usernameToFollow)).thenReturn(Mono.empty());
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
                                    () -> verify(userService, times(1))
                                            .saveUser(followerWithFollowingExpected),
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
            when(userService.createUser(username)).thenReturn(Mono.empty());
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
                                    () -> verify(userService, times(1))
                                            .saveUser(followerWithFollowingExpected),
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
            when(userService.createUser(username)).thenReturn(Mono.empty());
            when(userService.createUser(usernameToFollow)).thenReturn(Mono.empty());
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
                                    () -> verify(userService, times(1))
                                            .saveUser(followerWithFollowingExpected),
                                    () -> verifyNoMoreInteractions(userService))
                    ).verifyComplete();
        }

        @Test
        void when_follow_already_followed_user_should_not_follow_user() {

            String username = "user";
            String usernameToFollow = "userToFollow";
            UserNode followingWithFollowerExpected = userTestBuilder.withUsername(usernameToFollow).build(ObjectType.NODE);
            UserNode followerWithFollowingExpected = userTestBuilder.withUsername(usernameToFollow).build(ObjectType.NODE);
            FollowingRelationship followingRelationshipExpected = new FollowingRelationship(followingWithFollowerExpected);
            FollowingRelationship followedRelationshipExpected = new FollowingRelationship(followerWithFollowingExpected);
            followingWithFollowerExpected.getFollowing().add(followingRelationshipExpected);
            followerWithFollowingExpected.getFollowing().add(followedRelationshipExpected);

            when(userService.findUserByUsername(username)).thenReturn(Mono.just(followingWithFollowerExpected));
            when(userService.findUserByUsername(usernameToFollow)).thenReturn(Mono.just(followerWithFollowingExpected));
            when(userService.createUser(username)).thenReturn(Mono.empty());
            when(userService.createUser(usernameToFollow)).thenReturn(Mono.empty());

            Mono<Void> voidActualMono = followerService.followUser(username, usernameToFollow);

            StepVerifier.create(voidActualMono)
                    .then(() ->
                            assertAll(
                                    () -> verify(userService, times(1)).findUserByUsername(username),
                                    () -> verify(userService, times(1)).findUserByUsername(usernameToFollow),
                                    () -> verify(userService, times(1)).createUser(username),
                                    () -> verify(userService, times(1)).createUser(usernameToFollow),
                                    () -> verifyNoMoreInteractions(userService))
                    ).verifyComplete();
        }
    }
}
