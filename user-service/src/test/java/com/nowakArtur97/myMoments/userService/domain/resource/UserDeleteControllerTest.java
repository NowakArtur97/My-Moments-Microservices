package com.nowakArtur97.myMoments.userService.domain.resource;

import com.nowakArtur97.myMoments.userService.advice.AuthenticationControllerAdvice;
import com.nowakArtur97.myMoments.userService.advice.GlobalResponseEntityExceptionHandler;
import com.nowakArtur97.myMoments.userService.common.util.JwtUtil;
import com.nowakArtur97.myMoments.userService.domain.document.CustomUserDetailsService;
import com.nowakArtur97.myMoments.userService.exception.ForbiddenException;
import com.nowakArtur97.myMoments.userService.exception.ResourceNotFoundException;
import com.nowakArtur97.myMoments.userService.domain.document.UserService;
import com.nowakArtur97.myMoments.userService.testUtil.generator.NameWithSpacesGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("UserController_Tests")
class UserDeleteControllerTest {

    @LocalServerPort
    private int serverPort;

    private final String USER_BASE_PATH = "http://localhost:" + serverPort + "/api/v1/users/me";

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserObjectMapper userObjectMapper;

    @Mock
    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {

        UserController userController
                = new UserController(userService, customUserDetailsService, jwtUtil, userObjectMapper, modelMapper);

        GlobalResponseEntityExceptionHandler globalResponseEntityExceptionHandler
                = new GlobalResponseEntityExceptionHandler();

        mockMvc = MockMvcBuilders.standaloneSetup(userController, globalResponseEntityExceptionHandler)
                .setControllerAdvice(new AuthenticationControllerAdvice())
                .build();
    }

    @Test
    void when_delete_existing_user_should_not_return_content() {

        String header = "Bearer token";
        String username = "username";

        when(jwtUtil.extractUsernameFromHeader(header)).thenReturn(username);

        assertAll(
                () -> mockMvc.perform(delete(USER_BASE_PATH)
                        .header("Authorization", header))
                        .andExpect(status().isNoContent())
                        .andExpect(jsonPath("$").doesNotExist()),
                () -> verify(jwtUtil, times(1)).extractUsernameFromHeader(header),
                () -> verifyNoMoreInteractions(jwtUtil),
                () -> verify(userService, times(1)).deleteUser(username),
                () -> verifyNoMoreInteractions(userService),
                () -> verifyNoInteractions(customUserDetailsService),
                () -> verifyNoInteractions(userObjectMapper),
                () -> verifyNoInteractions(modelMapper));
    }

    @Test
    void when_delete_not_existing_user_should_return_error_response() {

        String header = "Bearer token";
        String username = "username";

        when(jwtUtil.extractUsernameFromHeader(header)).thenReturn(username);
        doThrow(new ResourceNotFoundException("User with username: '" + username + "' not found."))
                .when(userService).deleteUser(username);

        assertAll(
                () -> mockMvc.perform(delete(USER_BASE_PATH)
                        .header("Authorization", header))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty())
                        .andExpect(jsonPath("status", is(404)))
                        .andExpect(jsonPath("errors[0]", is("User with username: '" + username + "' not found.")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verify(jwtUtil, times(1)).extractUsernameFromHeader(header),
                () -> verifyNoMoreInteractions(jwtUtil),
                () -> verify(userService, times(1)).deleteUser(username),
                () -> verifyNoMoreInteractions(userService),
                () -> verifyNoInteractions(customUserDetailsService),
                () -> verifyNoInteractions(userObjectMapper),
                () -> verifyNoInteractions(modelMapper));
    }

    @Test
    void when_delete_not_owned_account_should_return_error_response() {

        String header = "Bearer token";
        String username = "username";

        when(jwtUtil.extractUsernameFromHeader(header)).thenReturn(username);
        doThrow(new ForbiddenException("User can only delete his own account.")).when(userService).deleteUser(username);

        assertAll(
                () -> mockMvc.perform(delete(USER_BASE_PATH)
                        .header("Authorization", header))
                        .andExpect(status().isForbidden())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty())
                        .andExpect(jsonPath("status", is(403)))
                        .andExpect(jsonPath("errors[0]", is("User can only delete his own account.")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verify(jwtUtil, times(1)).extractUsernameFromHeader(header),
                () -> verifyNoMoreInteractions(jwtUtil),
                () -> verify(userService, times(1)).deleteUser(username),
                () -> verifyNoMoreInteractions(userService),
                () -> verifyNoInteractions(customUserDetailsService),
                () -> verifyNoInteractions(userObjectMapper),
                () -> verifyNoInteractions(modelMapper));
    }
}
