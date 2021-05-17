package com.nowakArtur97.myMoments.userService.feature.resource;

import com.nowakArtur97.myMoments.userService.common.util.JwtUtil;
import com.nowakArtur97.myMoments.userService.feature.document.*;
import com.nowakArtur97.myMoments.userService.feature.testBuilder.UserProfileTestBuilder;
import com.nowakArtur97.myMoments.userService.feature.testBuilder.UserTestBuilder;
import com.nowakArtur97.myMoments.userService.exception.RoleNotFoundException;
import com.nowakArtur97.myMoments.userService.testUtil.enums.ObjectType;
import com.nowakArtur97.myMoments.userService.testUtil.generator.NameWithSpacesGenerator;
import com.nowakArtur97.myMoments.userService.testUtil.mapper.ObjectTestMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("UserController_Tests")
class UserUpdateValidationControllerTest {

    private final String USER_BASE_PATH = "http://localhost:8080/api/v1/users/me";

    @Value("${my-moments.default-user-role:USER_ROLE}")
    private String defaultUserRole;

    @Autowired
    private MockMvc mockMvc;

    private MockMultipartHttpServletRequestBuilder mockRequestBuilder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MongoTemplate mongoTemplate;
    
    private static UserProfileTestBuilder userProfileTestBuilder;
    private static UserTestBuilder userTestBuilder;

    private String token;

    @BeforeAll
    static void setUpBuilders() {

        userProfileTestBuilder = new UserProfileTestBuilder();
        userTestBuilder = new UserTestBuilder();
    }

    @BeforeEach
    void setUp() {

        RoleDocument roleDocument = roleRepository.findByName(defaultUserRole)
                .orElseThrow(() -> new RoleNotFoundException("Role: '" + defaultUserRole + "' not found."));

        UserProfileDocument userProfileDocument = (UserProfileDocument) userProfileTestBuilder.build(ObjectType.DOCUMENT);
        UserDocument userDocument = (UserDocument) userTestBuilder.withUsername("user456").withEmail("u4se5r@em6ail.com")
                .withPassword("userASD!").withMatchingPassword("userASD!").withProfile(userProfileDocument)
                .withRoles(Set.of(roleDocument)).build(ObjectType.DOCUMENT);

        userRepository.save(userDocument);

        token = jwtUtil.generateToken(new User(userDocument.getUsername(), userDocument.getPassword(),
                List.of(new SimpleGrantedAuthority(defaultUserRole))));

        mockRequestBuilder = MockMvcRequestBuilders.multipart(USER_BASE_PATH);
        mockRequestBuilder.with(request -> {
            request.setMethod("PUT");
            return request;
        });
    }

    @AfterEach
    void cleanUpDatabase() {

        mongoTemplate.getDb().drop();
    }

    @Test
    void when_update_user_with_without_data_should_return_error_response() {

        assertAll(
                () -> mockMvc
                        .perform(mockRequestBuilder
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors", hasItem("Username cannot be empty.")))
                        .andExpect(jsonPath("errors", hasItem("Password cannot be empty.")))
                        .andExpect(jsonPath("errors", hasItem("Matching password cannot be empty.")))
                        .andExpect(jsonPath("errors", hasItem("Email cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(4))));
    }

    @Test
    void when_update_user_with_with_only_image_should_return_error_response() {

        MockMultipartFile userData = new MockMultipartFile("file", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, "image".getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(mockRequestBuilder
                                .file(userData)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors", hasItem("Username cannot be empty.")))
                        .andExpect(jsonPath("errors", hasItem("Password cannot be empty.")))
                        .andExpect(jsonPath("errors", hasItem("Matching password cannot be empty.")))
                        .andExpect(jsonPath("errors", hasItem("Email cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(4))));
    }

    @Test
    void when_update_user_with_null_fields_should_return_error_response() {

        UserUpdateDTO userUpdateDTO = (UserUpdateDTO) userTestBuilder.withUsername(null)
                .withPassword(null).withMatchingPassword(null).withEmail(null).build(ObjectType.UPDATE_DTO);

        String userAsString = ObjectTestMapper.asJsonString(userUpdateDTO);

        MockMultipartFile userData = new MockMultipartFile("user", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, userAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(mockRequestBuilder
                                .file(userData)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors", hasItem("Username cannot be empty.")))
                        .andExpect(jsonPath("errors", hasItem("Password cannot be empty.")))
                        .andExpect(jsonPath("errors", hasItem("Matching password cannot be empty.")))
                        .andExpect(jsonPath("errors", hasItem("Email cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(4))));
    }

    @ParameterizedTest(name = "{index}: For User name: {0}")
    @EmptySource
    @ValueSource(strings = {" "})
    void when_update_user_with_blank_username_should_return_error_response(String invalidUsername) {

        UserUpdateDTO userUpdateDTO = (UserUpdateDTO) userTestBuilder.withUsername(invalidUsername).build(ObjectType.UPDATE_DTO);

        String userAsString = ObjectTestMapper.asJsonString(userUpdateDTO);

        MockMultipartFile userData = new MockMultipartFile("user", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, userAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(mockRequestBuilder
                                .file(userData)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors", hasItem("Username cannot be empty.")))
                        .andExpect(jsonPath("errors", hasItem("Username must be between 4 and 40 characters.")))
                        .andExpect(jsonPath("errors", hasSize(2))));
    }

    @Test
    void when_update_user_with_too_short_username_should_return_error_response() {

        UserUpdateDTO userUpdateDTO = (UserUpdateDTO) userTestBuilder.withUsername("u").build(ObjectType.UPDATE_DTO);

        String userAsString = ObjectTestMapper.asJsonString(userUpdateDTO);

        MockMultipartFile userData = new MockMultipartFile("user", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, userAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(mockRequestBuilder
                                .file(userData)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Username must be between 4 and 40 characters.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_update_user_with_too_long_username_should_return_error_response() {

        UserUpdateDTO userUpdateDTO = (UserUpdateDTO) userTestBuilder.withUsername("a".repeat(41)).build(ObjectType.UPDATE_DTO);

        String userAsString = ObjectTestMapper.asJsonString(userUpdateDTO);

        MockMultipartFile userData = new MockMultipartFile("user", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, userAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(mockRequestBuilder
                                .file(userData)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Username must be between 4 and 40 characters.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_update_user_with_username_already_taken_should_return_error_response() {

        UserUpdateDTO userUpdateDTO = (UserUpdateDTO) userTestBuilder.withUsername("user")
                .build(ObjectType.UPDATE_DTO);

        String userAsString = ObjectTestMapper.asJsonString(userUpdateDTO);

        MockMultipartFile userData = new MockMultipartFile("user", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, userAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(mockRequestBuilder
                                .file(userData)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Username: '" + userUpdateDTO.getUsername()
                                + "' is already taken.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_update_user_with_password_containing_username_should_return_error_response() {

        UserUpdateDTO userUpdateDTO = (UserUpdateDTO) userTestBuilder.withUsername("usertest")
                .withPassword("Pausertest1!").withMatchingPassword("Pausertest1!").build(ObjectType.UPDATE_DTO);

        String userAsString = ObjectTestMapper.asJsonString(userUpdateDTO);

        MockMultipartFile userData = new MockMultipartFile("user", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, userAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(mockRequestBuilder
                                .file(userData)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Password contains user name: '"
                                + userUpdateDTO.getUsername() + "'.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_update_user_with_password_containing_white_spaces_should_return_error_response() {

        UserUpdateDTO userUpdateDTO = (UserUpdateDTO) userTestBuilder.withPassword("Pass Word 123 !@#")
                .withMatchingPassword("Pass Word 123 !@#").build(ObjectType.UPDATE_DTO);

        String userAsString = ObjectTestMapper.asJsonString(userUpdateDTO);

        MockMultipartFile userData = new MockMultipartFile("user", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, userAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(mockRequestBuilder
                                .file(userData)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Password contains a whitespace character.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_update_user_with_password_containing_a_sequence_of_repeating_characters_should_return_error_response() {

        UserUpdateDTO userUpdateDTO = (UserUpdateDTO) userTestBuilder.withPassword("PAAA123a!@#")
                .withMatchingPassword("PAAA123a!@#").build(ObjectType.UPDATE_DTO);

        String userAsString = ObjectTestMapper.asJsonString(userUpdateDTO);

        MockMultipartFile userData = new MockMultipartFile("user", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, userAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(mockRequestBuilder
                                .file(userData)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Password contains a repetitive string: 'AAA'.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @ParameterizedTest(name = "{index}: For User password: {0}")
    @ValueSource(strings = {"123456", "qwerty", "iloveyou"})
    void when_update_user_with_popular_password_should_return_error_response(String popularPassword) {

        UserUpdateDTO userUpdateDTO = (UserUpdateDTO) userTestBuilder.withPassword(popularPassword)
                .withMatchingPassword(popularPassword).build(ObjectType.UPDATE_DTO);

        String userAsString = ObjectTestMapper.asJsonString(userUpdateDTO);

        MockMultipartFile userData = new MockMultipartFile("user", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, userAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(mockRequestBuilder
                                .file(userData)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors", hasItem("Password contains a popular phrase: '"
                                + popularPassword + "'."))));
    }

    @Test
    void when_update_user_with_too_short_password_should_return_error_response() {

        UserUpdateDTO userUpdateDTO = (UserUpdateDTO) userTestBuilder.withPassword("Pa1!")
                .withMatchingPassword("Pa1!").build(ObjectType.UPDATE_DTO);

        String userAsString = ObjectTestMapper.asJsonString(userUpdateDTO);

        MockMultipartFile userData = new MockMultipartFile("user", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, userAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(mockRequestBuilder
                                .file(userData)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Password must be 6 or more characters in length.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_update_user_with_too_long_password_should_return_error_response() {

        UserUpdateDTO userUpdateDTO = (UserUpdateDTO) userTestBuilder
                .withPassword("Pa1!Pa1!Pa1!Pa1!Pa1!Pa1!Pa1!Pa1!").withMatchingPassword("Pa1!Pa1!Pa1!Pa1!Pa1!Pa1!Pa1!Pa1!")
                .build(ObjectType.UPDATE_DTO);

        String userAsString = ObjectTestMapper.asJsonString(userUpdateDTO);

        MockMultipartFile userData = new MockMultipartFile("user", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, userAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(mockRequestBuilder
                                .file(userData)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]",
                                is("Password must be no more than 30 characters in length.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_update_user_with_lowercase_password_that_does_not_meet_at_least_two_requirements_should_return_error_response() {

        UserUpdateDTO userUpdateDTO = (UserUpdateDTO) userTestBuilder.withPassword("zdcsdfrg")
                .withMatchingPassword("zdcsdfrg").build(ObjectType.UPDATE_DTO);

        String userAsString = ObjectTestMapper.asJsonString(userUpdateDTO);

        MockMultipartFile userData = new MockMultipartFile("user", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, userAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(mockRequestBuilder
                                .file(userData)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors",
                                hasItem("Password matches 1 of 3 character rules, but 3 are required.")))
                        .andExpect(jsonPath("errors", hasItem("Password must contain 1 or more uppercase characters.")))
                        .andExpect(jsonPath("errors", hasItem("Password must contain 1 or more special characters.")))
                        .andExpect(jsonPath("errors", hasSize(3))));
    }

    @Test
    void when_update_user_with_uppercase_password_that_does_not_meet_at_least_two_requirements_should_return_error_response() {

        UserUpdateDTO userUpdateDTO = (UserUpdateDTO) userTestBuilder.withPassword("ONLYUPPERCASELETTERS")
                .withMatchingPassword("ONLYUPPERCASELETTERS").build(ObjectType.UPDATE_DTO);

        String userAsString = ObjectTestMapper.asJsonString(userUpdateDTO);

        MockMultipartFile userData = new MockMultipartFile("user", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, userAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(mockRequestBuilder
                                .file(userData)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors",
                                hasItem("Password matches 1 of 3 character rules, but 3 are required.")))
                        .andExpect(jsonPath("errors", hasItem("Password must contain 1 or more lowercase characters.")))
                        .andExpect(jsonPath("errors", hasItem("Password must contain 1 or more special characters.")))
                        .andExpect(jsonPath("errors", hasSize(3))));
    }

    @Test
    void when_update_user_with_blank_email_should_return_error_response() {

        UserUpdateDTO userUpdateDTO = (UserUpdateDTO) userTestBuilder.withEmail("     ").build(ObjectType.UPDATE_DTO);

        String userAsString = ObjectTestMapper.asJsonString(userUpdateDTO);

        MockMultipartFile userData = new MockMultipartFile("user", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, userAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(mockRequestBuilder
                                .file(userData)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors", hasItem("Email cannot be empty.")))
                        .andExpect(jsonPath("errors", hasItem("Email must be a valid email address.")))
                        .andExpect(jsonPath("errors", hasSize(2))));
    }

    @ParameterizedTest(name = "{index}: For User email: {0}")
    @ValueSource(strings = {"wrongformat", "wrong.format"})
    void when_update_user_with_an_incorrect_format_email_should_return_error_response(String invalidEmail) {

        UserUpdateDTO userUpdateDTO = (UserUpdateDTO) userTestBuilder.withEmail(invalidEmail).build(ObjectType.UPDATE_DTO);

        String userAsString = ObjectTestMapper.asJsonString(userUpdateDTO);

        MockMultipartFile userData = new MockMultipartFile("user", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, userAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(mockRequestBuilder
                                .file(userData)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Email must be a valid email address.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_update_user_with_email_already_taken_should_return_error_response() {

        UserUpdateDTO userUpdateDTO = (UserUpdateDTO) userTestBuilder.withEmail("user@email.com").build(ObjectType.UPDATE_DTO);

        String userAsString = ObjectTestMapper.asJsonString(userUpdateDTO);

        MockMultipartFile userData = new MockMultipartFile("user", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, userAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(mockRequestBuilder
                                .file(userData)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Email: '" + userUpdateDTO.getEmail()
                                + "' is already taken.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @ParameterizedTest(name = "{index}: For User password: {0}")
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void when_update_user_with_blank_password_should_return_error_response(String invalidPassword) {

        UserUpdateDTO userUpdateDTO = (UserUpdateDTO) userTestBuilder.withPassword(invalidPassword).build(ObjectType.UPDATE_DTO);

        String userAsString = ObjectTestMapper.asJsonString(userUpdateDTO);

        MockMultipartFile userData = new MockMultipartFile("user", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, userAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(mockRequestBuilder
                                .file(userData)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Password cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }


    @ParameterizedTest(name = "{index}: For User matching password: {0}")
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void when_update_user_with_blank_matching_password_should_return_error_response(String invalidMatchingPassword) {

        UserUpdateDTO userUpdateDTO = (UserUpdateDTO) userTestBuilder.withMatchingPassword
                (invalidMatchingPassword).build(ObjectType.UPDATE_DTO);

        String userAsString = ObjectTestMapper.asJsonString(userUpdateDTO);

        MockMultipartFile userData = new MockMultipartFile("user", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, userAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(mockRequestBuilder
                                .file(userData)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Matching password cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_update_user_with_not_matching_passwords_should_return_error_response() {

        UserUpdateDTO userUpdateDTO = (UserUpdateDTO) userTestBuilder.withPassword("Password123!@#")
                .withMatchingPassword("#@!321drowssaP").build(ObjectType.UPDATE_DTO);

        String userAsString = ObjectTestMapper.asJsonString(userUpdateDTO);

        MockMultipartFile userData = new MockMultipartFile("user", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, userAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(mockRequestBuilder
                                .file(userData)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Passwords don't match.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_update_user_with_incorrect_gender_should_return_error_response() {

        UserProfileDTO userProfileDTO = (UserProfileDTO) userProfileTestBuilder.withGender("invalid gender")
                .build(ObjectType.UPDATE_DTO);
        UserUpdateDTO userUpdateDTO = (UserUpdateDTO) userTestBuilder.withProfile(userProfileDTO).build(ObjectType.UPDATE_DTO);

        String userAsString = ObjectTestMapper.asJsonString(userUpdateDTO);

        MockMultipartFile userData = new MockMultipartFile("user", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, userAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(mockRequestBuilder
                                .file(userData)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]",
                                is("Incorrect gender entered. The options available are: MALE, FEMALE, and UNSPECIFIED.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_update_user_with_too_long_about_section_should_return_error_response() {

        UserProfileDTO userProfileDTO = (UserProfileDTO) userProfileTestBuilder.withAbout("a".repeat(251))
                .build(ObjectType.UPDATE_DTO);
        UserUpdateDTO userUpdateDTO = (UserUpdateDTO) userTestBuilder.withProfile(userProfileDTO).build(ObjectType.UPDATE_DTO);

        String userAsString = ObjectTestMapper.asJsonString(userUpdateDTO);

        MockMultipartFile userData = new MockMultipartFile("user", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, userAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(mockRequestBuilder
                                .file(userData)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("About section cannot be longer than 250.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_update_user_with_too_long_interests_section_should_return_error_response() {

        UserProfileDTO userProfileDTO = (UserProfileDTO) userProfileTestBuilder.withInterests("a".repeat(251))
                .build(ObjectType.UPDATE_DTO);
        UserUpdateDTO userUpdateDTO = (UserUpdateDTO) userTestBuilder.withProfile(userProfileDTO).build(ObjectType.UPDATE_DTO);

        String userAsString = ObjectTestMapper.asJsonString(userUpdateDTO);

        MockMultipartFile userData = new MockMultipartFile("user", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, userAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(mockRequestBuilder
                                .file(userData)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Interest section cannot be longer than 250.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_update_user_with_too_long_languages_section_should_return_error_response() {

        UserProfileDTO userProfileDTO = (UserProfileDTO) userProfileTestBuilder.withLanguages("a".repeat(251))
                .build(ObjectType.UPDATE_DTO);
        UserUpdateDTO userUpdateDTO = (UserUpdateDTO) userTestBuilder.withProfile(userProfileDTO).build(ObjectType.UPDATE_DTO);

        String userAsString = ObjectTestMapper.asJsonString(userUpdateDTO);

        MockMultipartFile userData = new MockMultipartFile("user", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, userAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(mockRequestBuilder
                                .file(userData)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Languages section cannot be longer than 250.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_update_user_with_too_long_location_section_should_return_error_response() {

        UserProfileDTO userProfileDTO = (UserProfileDTO) userProfileTestBuilder.withLocation("a".repeat(51))
                .build(ObjectType.UPDATE_DTO);
        UserUpdateDTO userUpdateDTO = (UserUpdateDTO) userTestBuilder.withProfile(userProfileDTO).build(ObjectType.UPDATE_DTO);

        String userAsString = ObjectTestMapper.asJsonString(userUpdateDTO);

        MockMultipartFile userData = new MockMultipartFile("user", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, userAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(mockRequestBuilder
                                .file(userData)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Location cannot be longer than 50.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_update_user_with_not_existing_username_should_return_error_response() {

        String notExistingUsername = "iAmNotExist";

        UserProfileDTO userProfileDTO = (UserProfileDTO) userProfileTestBuilder.build(ObjectType.UPDATE_DTO);
        UserUpdateDTO userUpdateDTO = (UserUpdateDTO) userTestBuilder.withProfile(userProfileDTO).build(ObjectType.UPDATE_DTO);

        String userAsString = ObjectTestMapper.asJsonString(userUpdateDTO);

        MockMultipartFile userData = new MockMultipartFile("user", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, userAsString.getBytes(StandardCharsets.UTF_8));

        token = jwtUtil.generateToken(new User(notExistingUsername, notExistingUsername,
                List.of(new SimpleGrantedAuthority(defaultUserRole))));

        assertAll(
                () -> mockMvc
                        .perform(mockRequestBuilder
                                .file(userData)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isUnauthorized())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(401)))
                        .andExpect(jsonPath("errors[0]",
                                is("User with name/email: '" + notExistingUsername + "' not found.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }
}
