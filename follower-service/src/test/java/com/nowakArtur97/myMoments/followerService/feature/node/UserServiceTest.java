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

        userService = new UserService(userRepository);
    }

    @Nested
    class CreateUserTest {

        @Test
        void when_create_user_should_return_user() {

            String username = "user";
            UserNode userExpected = userTestBuilder.withUsername(username).build(ObjectType.NODE);

            when(userRepository.save(userExpected)).thenReturn(Mono.just(userExpected));

            Mono<UserNode> userActualMono = userService.createUser(username);

            StepVerifier.create(userActualMono)
                    .thenConsumeWhile(
                            userActual -> {
                                assertUser(userExpected, userActual);
                                assertAll(
                                        () -> verify(userRepository, times(1)).save(userExpected),
                                        () -> verifyNoMoreInteractions(userRepository));
                                return true;
                            }
                    ).verifyComplete();
        }
    }

    @Nested
    class SaveUserTest {

        @Test
        void when_save_user_should_return_user() {

            UserNode userExpected = userTestBuilder.build(ObjectType.NODE);

            when(userRepository.save(userExpected)).thenReturn(Mono.just(userExpected));

            Mono<UserNode> userActualMono = userService.saveUser(userExpected);

            StepVerifier.create(userActualMono)
                    .thenConsumeWhile(
                            userActual -> {
                                assertUser(userExpected, userActual);
                                assertAll(
                                        () -> verify(userRepository, times(1)).save(userExpected),
                                        () -> verifyNoMoreInteractions(userRepository));
                                return true;
                            }
                    ).verifyComplete();
        }
    }

    @Nested
    class FindUserTest {

        @Test
        void when_find_existing_user_by_username_should_return_user() {

            String username = "user";
            UserNode userExpected = userTestBuilder.withUsername(username).build(ObjectType.NODE);

            when(userRepository.findByUsername(username)).thenReturn(Mono.just(userExpected));

            Mono<UserNode> userActualMono = userService.findUserByUsername(username);

            StepVerifier.create(userActualMono)
                    .thenConsumeWhile(
                            userActual -> {
                                assertUser(userExpected, userActual);
                                assertAll(
                                        () -> verify(userRepository, times(1)).findByUsername(username),
                                        () -> verifyNoMoreInteractions(userRepository));
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
                                    () -> verifyNoMoreInteractions(userRepository))
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
