package com.nowakArtur97.myMoments.gatewayService.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("UserService_Tests")
class UserServiceTest {

    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {

        userService = new UserService(userRepository);
    }

    @Test
    @DisplayName("when find existing user by username or email")
    void when_find_existing_user_by_username_should_return_user() {

        UserDocument userExpected = new UserDocument("username", "email@email.com", "password",
                Set.of(new RoleDocument("USER_ROLE")));

        when(userRepository.findByUsername(userExpected.getUsername())).thenReturn(Mono.just(userExpected));

        Mono<UserDocument> userActualMono = userService.findByUsername(userExpected.getUsername());

        StepVerifier.create(userActualMono)
                .thenConsumeWhile(
                        userActual -> {
                            assertAll(() -> assertEquals(userExpected, userActual,
                                    () -> "should return user: " + userExpected + ", but was" + userActual),
                                    () -> assertEquals(userExpected.getUsername(), userActual.getUsername(),
                                            () -> "should return user with username: " + userExpected.getUsername() + ", but was"
                                                    + userActual.getUsername()),
                                    () -> assertEquals(userExpected.getPassword(), userActual.getPassword(),
                                            () -> "should return user with user password: " + userExpected.getPassword() + ", but was"
                                                    + userActual.getPassword()),
                                    () -> assertEquals(userExpected.getEmail(), userActual.getEmail(),
                                            () -> "should return user with user email: " + userExpected.getEmail() + ", but was"
                                                    + userActual.getEmail()),
                                    () -> assertEquals(userExpected.getRoles(), userActual.getRoles(),
                                            () -> "should return user with user roles: " + userExpected.getRoles() + ", but was"
                                                    + userActual.getRoles()),
                                    () -> verify(userRepository, times(1)).findByUsername(
                                            userExpected.getUsername()),
                                    () -> verifyNoMoreInteractions(userRepository));
                            return true;
                        }
                ).verifyComplete();
    }

    @Test
    @DisplayName("when find not existing user by username or email")
    void when_find_not_existing_user_by_username_should_return_empty_optional() {

        String notExistingUsername = "notExistingUsername";

        when(userRepository.findByUsername(notExistingUsername)).thenReturn(Mono.empty());

        Mono<UserDocument> userActualMono = userService.findByUsername(notExistingUsername);

        StepVerifier.create(userActualMono)
                .expectNextCount(0)
                .then(() ->
                        assertAll(
                                () -> verify(userRepository, times(1)).findByUsername(notExistingUsername))
                )
                .verifyComplete();
    }
}
