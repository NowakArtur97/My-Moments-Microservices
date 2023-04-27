package com.nowakArtur97.myMoments.userService.feature.authentication;

import com.nowakArtur97.myMoments.userService.advice.AuthenticationControllerAdvice;
import com.nowakArtur97.myMoments.userService.advice.GlobalResponseEntityExceptionHandler;
import com.nowakArtur97.myMoments.userService.common.util.JwtUtil;
import com.nowakArtur97.myMoments.userService.feature.document.CustomUserDetailsService;
import com.nowakArtur97.myMoments.userService.feature.document.UserDocument;
import com.nowakArtur97.myMoments.userService.feature.document.UserProfileDocument;
import com.nowakArtur97.myMoments.userService.feature.document.UserService;
import com.nowakArtur97.myMoments.userService.feature.resource.UserModel;
import com.nowakArtur97.myMoments.userService.feature.resource.UserProfileModel;
import com.nowakArtur97.myMoments.userService.feature.testBuilder.UserProfileTestBuilder;
import com.nowakArtur97.myMoments.userService.feature.testBuilder.UserTestBuilder;
import com.nowakArtur97.myMoments.userService.testUtil.enums.ObjectType;
import com.nowakArtur97.myMoments.userService.testUtil.generator.NameWithSpacesGenerator;
import com.nowakArtur97.myMoments.userService.testUtil.mapper.ObjectTestMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.nowakArtur97.myMoments.userService.feature.testBuilder.RoleTestBuilder.DEFAULT_ROLE_ENTITY;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("AuthenticationController_Tests")
class AuthenticationControllerTest {

    @LocalServerPort
    private int serverPort;

    private final String AUTHENTICATION_BASE_PATH = "http://localhost:" + serverPort + "/api/v1/authentication";
    private final String USER_ROLE = "USER_ROLE";
    private final int EXPIRATION_TIME_IN_MILLISECONDS = 36000000;

    private MockMvc mockMvc;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private ModelMapper modelMapper;

    private static UserTestBuilder userTestBuilder;
    private static UserProfileTestBuilder userProfileTestBuilder;

    @BeforeAll
    static void setUpBuilders() {

        userTestBuilder = new UserTestBuilder();
        userProfileTestBuilder = new UserProfileTestBuilder();
    }

    @BeforeEach
    void setUp() {

        AuthenticationController authenticationController
                = new AuthenticationController(customUserDetailsService, userService, authenticationManager, jwtUtil, modelMapper);

        GlobalResponseEntityExceptionHandler globalResponseEntityExceptionHandler
                = new GlobalResponseEntityExceptionHandler();

        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController, globalResponseEntityExceptionHandler)
                .setControllerAdvice(new AuthenticationControllerAdvice())
                .build();

        ReflectionTestUtils.setField(authenticationController, "validity", EXPIRATION_TIME_IN_MILLISECONDS);
    }

    @Test
    void when_authenticate_valid_user_should_generate_token() {

        AuthenticationRequest authenticationRequest = (AuthenticationRequest) userTestBuilder.build(ObjectType.REQUEST);
        String userName = authenticationRequest.getUsername();
        String password = authenticationRequest.getPassword();

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                userName, password);
        User userDetails = new User(userName, password, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        String token = "generated token";
        UserProfileDocument userProfile = (UserProfileDocument) userProfileTestBuilder.build(ObjectType.DOCUMENT);
        UserDocument userDocument = (UserDocument) userTestBuilder.withUsername(userName).withProfile(userProfile)
                .build(ObjectType.DOCUMENT);
        UserProfileModel userProfileModel = (UserProfileModel) userProfileTestBuilder.build(ObjectType.MODEL);
        UserModel userModel = (UserModel) userTestBuilder.withUsername(userName).withProfile(userProfileModel)
                .withRoles(Set.of(DEFAULT_ROLE_ENTITY)).build(ObjectType.MODEL);

        when(customUserDetailsService.loadUserByUsername(userName)).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn(token);
        when(userService.findByUsername(userName)).thenReturn(Optional.of(userDocument));
        when(modelMapper.map(userDocument, UserModel.class)).thenReturn(userModel);

        assertAll(
                () -> mockMvc
                        .perform(post(AUTHENTICATION_BASE_PATH)
                                .content(ObjectTestMapper.asJsonString(authenticationRequest))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("username", is(userDocument.getUsername())))
                        .andExpect(jsonPath("email", is(userDocument.getEmail())))
                        .andExpect(jsonPath("password").doesNotExist())
                        .andExpect(jsonPath("authenticationResponse.token", is(token)))
                        .andExpect(jsonPath("authenticationResponse.expirationTimeInMilliseconds",
                                is(EXPIRATION_TIME_IN_MILLISECONDS)))
                        .andExpect(jsonPath("profile.about", is(userProfile.getAbout())))
                        .andExpect(jsonPath("profile.gender", is(userProfile.getGender().name())))
                        .andExpect(jsonPath("profile.interests", is(userProfile.getInterests())))
                        .andExpect(jsonPath("profile.languages", is(userProfile.getLanguages())))
                        .andExpect(jsonPath("profile.location", is(userProfile.getLocation())))
                        .andExpect(jsonPath("profile.image").isEmpty())
                        .andExpect(jsonPath("roles[0].name", is(USER_ROLE))),
                () -> verify(customUserDetailsService, times(1)).loadUserByUsername(userName),
                () -> verifyNoMoreInteractions(customUserDetailsService),
                () -> verify(authenticationManager, times(1))
                        .authenticate(usernamePasswordAuthenticationToken),
                () -> verifyNoMoreInteractions(authenticationManager),
                () -> verify(jwtUtil, times(1)).generateToken(userDetails),
                () -> verifyNoMoreInteractions(jwtUtil),
                () -> verify(userService, times(1)).findByUsername(userName),
                () -> verifyNoMoreInteractions(userService),
                () -> verify(modelMapper, times(1)).map(userDocument, UserModel.class),
                () -> verifyNoMoreInteractions(modelMapper));
    }

    @Test
    void when_authenticate_not_existing_user_should_return_error_response() {

        AuthenticationRequest authenticationRequest = (AuthenticationRequest) userTestBuilder.build(ObjectType.REQUEST);
        String userName = authenticationRequest.getUsername();

        when(customUserDetailsService.loadUserByUsername(userName))
                .thenThrow(new UsernameNotFoundException("User with name/email: '" + userName + "' not found."));

        assertAll(
                () -> mockMvc
                        .perform(post(AUTHENTICATION_BASE_PATH)
                                .content(ObjectTestMapper.asJsonString(authenticationRequest))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isUnauthorized())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty())
                        .andExpect(jsonPath("status", is(401)))
                        .andExpect(jsonPath("errors[0]",
                                is("User with name/email: '" + userName + "' not found.")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verify(customUserDetailsService, times(1)).loadUserByUsername(userName),
                () -> verifyNoMoreInteractions(customUserDetailsService),
                () -> verifyNoInteractions(authenticationManager),
                () -> verifyNoInteractions(jwtUtil),
                () -> verifyNoInteractions(modelMapper),
                () -> verifyNoInteractions(userService));
    }

    @Test
    void when_authenticate_user_with_incorrect_data_should_return_error_response() {

        AuthenticationRequest authenticationRequest = (AuthenticationRequest) userTestBuilder.build(ObjectType.REQUEST);
        String userName = authenticationRequest.getUsername();
        String password = authenticationRequest.getPassword();

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                userName, password);
        User userDetails = new User(userName, password, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        when(customUserDetailsService.loadUserByUsername(userName)).thenReturn(userDetails);
        when(authenticationManager.authenticate(usernamePasswordAuthenticationToken)).thenThrow(new BadCredentialsException(""));

        assertAll(
                () -> mockMvc
                        .perform(post(AUTHENTICATION_BASE_PATH)
                                .content(ObjectTestMapper.asJsonString(authenticationRequest))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isUnauthorized())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty())
                        .andExpect(jsonPath("status", is(401)))
                        .andExpect(jsonPath("errors[0]", is("Invalid login credentials.")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verify(customUserDetailsService, times(1)).loadUserByUsername(userName),
                () -> verifyNoMoreInteractions(customUserDetailsService),
                () -> verify(authenticationManager,
                        times(1)).authenticate(usernamePasswordAuthenticationToken),
                () -> verifyNoMoreInteractions(authenticationManager),
                () -> verifyNoInteractions(jwtUtil),
                () -> verifyNoInteractions(modelMapper),
                () -> verifyNoInteractions(userService));
    }
}
