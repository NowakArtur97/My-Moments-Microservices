package com.nowakArtur97.myMoments.postService.feature.user;


import com.nowakArtur97.myMoments.postService.testUtil.generator.NameWithSpacesGenerator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("UserService_Tests")
class UserServiceTest {

    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private static UserTestBuilder userTestBuilder;

    @BeforeAll
    static void setUpBuilders() {

        userTestBuilder = new UserTestBuilder();
    }

    @BeforeEach
    void setUp() {

        userService = new UserService(userRepository);
    }

    @Test
    void when_find_existing_user_by_username_should_return_user() {

        UserDocument userExpected = userTestBuilder.build();

        when(userRepository.findByUsername(userExpected.getUsername())).thenReturn(Mono.just(userExpected));

        Mono<UserDocument> userActualMono = userService.findByUsername(userExpected.getUsername());

        StepVerifier.create(userActualMono)
                .thenConsumeWhile(
                        userActual -> {
                            assertAll(() -> assertNotNull(userActual,
                                    () -> "should return user: " + userExpected + ", but was: " + userActual),
                                    () -> assertEquals(userExpected, userActual,
                                            () -> "should return user: " + userExpected + ", but was: " + userActual),
                                    () -> assertEquals(userExpected.getUsername(), userActual.getUsername(),
                                            () -> "should return user with username: " + userExpected.getUsername() + ", but was: "
                                                    + userActual.getUsername()),
                                    () -> assertEquals(userExpected.getPassword(), userActual.getPassword(),
                                            () -> "should return user with user password: " + userExpected.getPassword() + ", but was: "
                                                    + userActual.getPassword()),
                                    () -> assertEquals(userExpected.getEmail(), userActual.getEmail(),
                                            () -> "should return user with user email: " + userExpected.getEmail() + ", but was: "
                                                    + userActual.getEmail()),
                                    () -> assertEquals(userExpected.getRoles(), userActual.getRoles(),
                                            () -> "should return user with user roles: " + userExpected.getRoles() + ", but was: "
                                                    + userActual.getRoles()),
                                    () -> verify(userRepository, times(1))
                                            .findByUsername(userExpected.getUsername()),
                                    () -> verifyNoMoreInteractions(userRepository));
                            return true;
                        }
                ).verifyComplete();
    }

    @Test
    void when_find_not_existing_user_by_username_should_return_empty_mono() {

        String notExistingUsername = "notExistingUsername";

        when(userRepository.findByUsername(notExistingUsername)).thenReturn(Mono.empty());

        Mono<UserDocument> userActualMono = userService.findByUsername(notExistingUsername);

        StepVerifier.create(userActualMono)
                .expectNextCount(0)
                .then(() -> {
                            assertAll(
                                    () -> verify(userRepository, times(1))
                                            .findByUsername(notExistingUsername),
                                    () -> verifyNoMoreInteractions(userRepository));
                        }
                )
                .verifyComplete();
    }

    @Test
    void when_find_existing_user_by_username_or_email_should_return_user() {

        UserDocument userExpected = userTestBuilder.build();

        when(userRepository.findByUsernameOrEmail(userExpected.getUsername(), userExpected.getUsername()))
                .thenReturn(Mono.just(userExpected));

        Mono<UserDocument> userActualMono = userService.findByUsernameOrEmail(userExpected.getUsername());

        StepVerifier.create(userActualMono)
                .thenConsumeWhile(
                        userActual -> {
                            assertAll(() -> assertNotNull(userActual,
                                    () -> "should return user: " + userExpected + ", but was: " + userActual),
                                    () -> assertEquals(userExpected, userActual,
                                            () -> "should return user: " + userExpected + ", but was: " + userActual),
                                    () -> assertEquals(userExpected.getUsername(), userActual.getUsername(),
                                            () -> "should return user with username: " + userExpected.getUsername() + ", but was: "
                                                    + userActual.getUsername()),
                                    () -> assertEquals(userExpected.getPassword(), userActual.getPassword(),
                                            () -> "should return user with user password: " + userExpected.getPassword() + ", but was: "
                                                    + userActual.getPassword()),
                                    () -> assertEquals(userExpected.getEmail(), userActual.getEmail(),
                                            () -> "should return user with user email: " + userExpected.getEmail() + ", but was: "
                                                    + userActual.getEmail()),
                                    () -> assertEquals(userExpected.getRoles(), userActual.getRoles(),
                                            () -> "should return user with user roles: " + userExpected.getRoles() + ", but was: "
                                                    + userActual.getRoles()),
                                    () -> verify(userRepository, times(1))
                                            .findByUsernameOrEmail(userExpected.getUsername(), userExpected.getUsername()),
                                    () -> verifyNoMoreInteractions(userRepository));
                            return true;
                        }
                ).verifyComplete();
    }

    @Test
    void when_find_not_existing_user_by_username_or_email_should_return_empty_mono() {

        String notExistingUsernameOrEmail = "notExistingUsername";

        when(userRepository.findByUsernameOrEmail(notExistingUsernameOrEmail, notExistingUsernameOrEmail))
                .thenReturn(Mono.empty());

        Mono<UserDocument> userActualMono = userService.findByUsernameOrEmail(notExistingUsernameOrEmail);

        StepVerifier.create(userActualMono)
                .expectNextCount(0)
                .then(() -> {
                            assertAll(
                                    () -> verify(userRepository, times(1))
                                            .findByUsernameOrEmail(notExistingUsernameOrEmail, notExistingUsernameOrEmail),
                                    () -> verifyNoMoreInteractions(userRepository));
                        }
                )
                .verifyComplete();
    }
}
