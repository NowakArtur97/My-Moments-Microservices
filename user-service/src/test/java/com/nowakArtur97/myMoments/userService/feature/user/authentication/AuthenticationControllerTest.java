package com.nowakArtur97.myMoments.userService.feature.user.authentication;

import com.nowakArtur97.myMoments.userService.advice.AuthenticationControllerAdvice;
import com.nowakArtur97.myMoments.userService.advice.GlobalResponseEntityExceptionHandler;
import com.nowakArtur97.myMoments.userService.common.util.JwtUtil;
import com.nowakArtur97.myMoments.userService.feature.user.document.CustomUserDetailsService;
import com.nowakArtur97.myMoments.userService.feature.user.testBuilder.UserTestBuilder;
import com.nowakArtur97.myMoments.userService.testUtil.enums.ObjectType;
import com.nowakArtur97.myMoments.userService.testUtil.generator.NameWithSpacesGenerator;
import com.nowakArtur97.myMoments.userService.testUtil.mapper.ObjectTestMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
    private final int EXPIRATION_TIME_IN_MILLISECONDS = 36000000;

    private MockMvc mockMvc;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    private static UserTestBuilder userTestBuilder;

    @BeforeAll
    static void setUpBuilders() {

        userTestBuilder = new UserTestBuilder();
    }

    @BeforeEach
    void setUp() {

        AuthenticationController authenticationController
                = new AuthenticationController(customUserDetailsService, authenticationManager, jwtUtil);

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

        when(customUserDetailsService.loadUserByUsername(userName)).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn(token);

        assertAll(
                () -> mockMvc
                        .perform(post(AUTHENTICATION_BASE_PATH)
                                .content(ObjectTestMapper.asJsonString(authenticationRequest))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("token", is(token)))
                        .andExpect(jsonPath("expirationTimeInMilliseconds", is(EXPIRATION_TIME_IN_MILLISECONDS))),
                () -> verify(customUserDetailsService, times(1)).loadUserByUsername(userName),
                () -> verifyNoMoreInteractions(customUserDetailsService),
                () -> verify(authenticationManager, times(1))
                        .authenticate(usernamePasswordAuthenticationToken),
                () -> verifyNoMoreInteractions(authenticationManager),
                () -> verify(jwtUtil, times(1)).generateToken(userDetails),
                () -> verifyNoMoreInteractions(jwtUtil));
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
                () -> verifyNoInteractions(jwtUtil));
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
                () -> verifyNoInteractions(jwtUtil));
    }
}