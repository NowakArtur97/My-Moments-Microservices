package com.nowakArtur97.myMoments.userService.feature.user.document;

import com.nowakArtur97.myMoments.userService.feature.user.testBuilder.UserProfileTestBuilder;
import com.nowakArtur97.myMoments.userService.feature.user.testBuilder.UserTestBuilder;
import com.nowakArtur97.myMoments.userService.testUtil.enums.ObjectType;
import com.nowakArtur97.myMoments.userService.testUtil.generator.NameWithSpacesGenerator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("CustomUserDetailsService_Tests")
class CustomUserDetailsServiceTest {

    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private UserRepository userRepository;

    private static UserProfileTestBuilder userProfileTestBuilder;
    private static UserTestBuilder userTestBuilder;

    @BeforeAll
    static void setUpBuilders() {

        userProfileTestBuilder = new UserProfileTestBuilder();
        userTestBuilder = new UserTestBuilder();
    }

    @BeforeEach
    void setUp() {

        customUserDetailsService = new CustomUserDetailsService(userRepository);
    }

    @Test
    void when_load_user_by_user_name_should_return_user_details() {

        String expectedUsername = "username";

        UserProfileDocument userProfileExpected = (UserProfileDocument) userProfileTestBuilder.build(ObjectType.DOCUMENT);
        UserDocument userDocumentExpected = (UserDocument) userTestBuilder.withUsername(expectedUsername)
                .withProfile(userProfileExpected).withRoles(Set.of(new RoleDocument("USER_ROLE"))).build(ObjectType.DOCUMENT);
        User userDetailsExpected = new User(userDocumentExpected.getUsername(), userDocumentExpected.getPassword(),
                List.of(new SimpleGrantedAuthority("USER_ROLE")));

        when(userRepository.findByUsernameOrEmail(expectedUsername, expectedUsername)).thenReturn(Optional.of(userDocumentExpected));

        UserDetails userDetailsActual = customUserDetailsService.loadUserByUsername(expectedUsername);

        assertAll(() -> assertEquals(userDetailsExpected, userDetailsActual, () -> "should return user details: "
                        + userDetailsExpected + ", but was: " + userDetailsActual),
                () -> assertEquals(userDetailsExpected.getUsername(), userDetailsActual.getUsername(),
                        () -> "should return user details with user name: " + userDetailsExpected.getUsername()
                                + ", but was: " + userDetailsActual.getUsername()),
                () -> assertEquals(userDetailsExpected.getPassword(), userDetailsActual.getPassword(),
                        () -> "should return user details with user password: " + userDetailsExpected.getPassword()
                                + ", but was: " + userDetailsActual.getPassword()),
                () -> assertEquals(userDetailsExpected.getAuthorities(), userDetailsActual.getAuthorities(),
                        () -> "should return user details with authorities: " + userDetailsExpected.getAuthorities()
                                + ", but was: " + userDetailsActual.getAuthorities()),
                () -> verify(userRepository, times(1))
                        .findByUsernameOrEmail(expectedUsername, expectedUsername),
                () -> verifyNoMoreInteractions(userRepository));
    }

    @Test
    void when_load_user_by_email_should_return_user_details() {

        String expectedEmail = "user@email.com";

        UserProfileDocument userProfileExpected = (UserProfileDocument) userProfileTestBuilder.build(ObjectType.DOCUMENT);
        UserDocument userDocumentExpected = (UserDocument) userTestBuilder.withEmail(expectedEmail).withProfile(userProfileExpected)
                .withRoles(Set.of(new RoleDocument("USER_ROLE"))).build(ObjectType.DOCUMENT);
        User userDetailsExpected = new User(userDocumentExpected.getUsername(), userDocumentExpected.getPassword(),
                List.of(new SimpleGrantedAuthority("USER_ROLE")));

        when(userRepository.findByUsernameOrEmail(expectedEmail, expectedEmail)).thenReturn(Optional.of(userDocumentExpected));

        UserDetails userDetailsActual = customUserDetailsService.loadUserByUsername(expectedEmail);

        assertAll(() -> assertEquals(userDetailsExpected, userDetailsActual, () -> "should return user details: "
                        + userDetailsExpected + ", but was: " + userDetailsActual),
                () -> assertEquals(userDetailsExpected.getUsername(), userDetailsActual.getUsername(),
                        () -> "should return user details with user name: " + userDetailsExpected.getUsername()
                                + ", but was: " + userDetailsActual.getUsername()),
                () -> assertEquals(userDetailsExpected.getPassword(), userDetailsActual.getPassword(),
                        () -> "should return user details with user password: " + userDetailsExpected.getPassword()
                                + ", but was: " + userDetailsActual.getPassword()),
                () -> assertEquals(userDetailsExpected.getAuthorities(), userDetailsActual.getAuthorities(),
                        () -> "should return user details with authorities: " + userDetailsExpected.getAuthorities()
                                + ", but was: " + userDetailsActual.getAuthorities()),
                () -> verify(userRepository, times(1)).findByUsernameOrEmail(expectedEmail, expectedEmail),
                () -> verifyNoMoreInteractions(userRepository));
    }

    @Test
    void when_load_not_existing_user_by_user_name_or_email_should_throw_exception() {

        String userName = "user";

        when(userRepository.findByUsernameOrEmail(userName, userName)).thenReturn(Optional.empty());

        assertAll(
                () -> assertThrows(UsernameNotFoundException.class,
                        () -> customUserDetailsService.loadUserByUsername(userName),
                        () -> "should throw UsernameNotFoundException but wasn't"),
                () -> verify(userRepository, times(1)).findByUsernameOrEmail(userName, userName),
                () -> verifyNoMoreInteractions(userRepository));
    }

    @Test
    void when_get_user_authorities_should_return_list_of_authorities() {

        Set<RoleDocument> authoritiesExpected = Set.of(new RoleDocument("USER_ROLE"));
        UserProfileDocument userProfileExpected = (UserProfileDocument) userProfileTestBuilder.build(ObjectType.DOCUMENT);
        UserDocument userDocumentExpected = (UserDocument) userTestBuilder.withRoles(authoritiesExpected)
                .withProfile(userProfileExpected).build(ObjectType.DOCUMENT);

        List<GrantedAuthority> authoritiesActual = customUserDetailsService.getAuthorities(userDocumentExpected.getRoles());

        assertAll(
                () -> assertEquals(authoritiesExpected.size(), authoritiesActual.size(),
                        () -> "should return: " + authoritiesExpected.size() + " authorities, but was: "
                                + authoritiesActual.size()),
                () -> assertEquals(authoritiesActual.get(0).getAuthority(), "USER_ROLE",
                        () -> "should return user authority, but was: " + authoritiesActual.get(0).getAuthority()),
                () -> verifyNoInteractions(userRepository));
    }
}
