package com.nowakArtur97.myMoments.userService.domain.resource;

import com.nowakArtur97.myMoments.userService.domain.testBuilder.UserProfileTestBuilder;
import com.nowakArtur97.myMoments.userService.domain.testBuilder.UserTestBuilder;
import com.nowakArtur97.myMoments.userService.testUtil.enums.ObjectType;
import com.nowakArtur97.myMoments.userService.testUtil.generator.NameWithSpacesGenerator;
import com.nowakArtur97.myMoments.userService.testUtil.mapper.ObjectTestMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("UserRegistrationController_Tests")
class UserRegistrationValidationControllerTest {

    @LocalServerPort
    private int serverPort;

    private final String REGISTRATION_BASE_PATH = "http://localhost:" + serverPort + "/api/v1/registration/register";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MongoTemplate mongoTemplate;

    private static UserProfileTestBuilder userProfileTestBuilder;
    private static UserTestBuilder userTestBuilder;

    @BeforeAll
    static void setUpBuilders() {

        userProfileTestBuilder = new UserProfileTestBuilder();
        userTestBuilder = new UserTestBuilder();
    }

    @AfterEach
    void cleanUpDatabase() {

        mongoTemplate.getDb().drop();
    }

    @Test
    void when_register_user_with_without_data_should_return_error_response() {

        assertAll(
                () -> mockMvc
                        .perform(multipart(REGISTRATION_BASE_PATH)
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
    void when_register_user_with_with_only_image_should_return_error_response() {

        MockMultipartFile userData = new MockMultipartFile("file", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, "image".getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(multipart(REGISTRATION_BASE_PATH)
                                .file(userData)
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
    void when_register_user_with_null_fields_should_return_error_response() {

        UserRegistrationDTO userRegistrationDTO = (UserRegistrationDTO) userTestBuilder.withUsername(null)
                .withPassword(null).withMatchingPassword(null).withEmail(null).build(ObjectType.CREATE_DTO);

        String userAsString = ObjectTestMapper.asJsonString(userRegistrationDTO);

        MockMultipartFile userData = new MockMultipartFile("user", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, userAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(multipart(REGISTRATION_BASE_PATH)
                                .file(userData)
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
    void when_register_user_with_blank_username_should_return_error_response(String invalidUsername) {

        UserRegistrationDTO userRegistrationDTO = (UserRegistrationDTO) userTestBuilder.withUsername(invalidUsername)
                .build(ObjectType.CREATE_DTO);

        String userAsString = ObjectTestMapper.asJsonString(userRegistrationDTO);

        MockMultipartFile userData = new MockMultipartFile("user", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, userAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(multipart(REGISTRATION_BASE_PATH)
                                .file(userData)
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors", hasItem("Username cannot be empty.")))
                        .andExpect(jsonPath("errors", hasItem("Username must be between 4 and 40 characters.")))
                        .andExpect(jsonPath("errors", hasSize(2))));
    }

    @Test
    void when_register_user_with_too_short_username_should_return_error_response() {

        UserRegistrationDTO userRegistrationDTO = (UserRegistrationDTO) userTestBuilder.withUsername("u")
                .build(ObjectType.CREATE_DTO);

        String userAsString = ObjectTestMapper.asJsonString(userRegistrationDTO);

        MockMultipartFile userData = new MockMultipartFile("user", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, userAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(multipart(REGISTRATION_BASE_PATH)
                                .file(userData)
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Username must be between 4 and 40 characters.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_register_user_with_too_long_username_should_return_error_response() {

        UserRegistrationDTO userRegistrationDTO = (UserRegistrationDTO) userTestBuilder.withUsername("a".repeat(41))
                .build(ObjectType.CREATE_DTO);

        String userAsString = ObjectTestMapper.asJsonString(userRegistrationDTO);

        MockMultipartFile userData = new MockMultipartFile("user", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, userAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(multipart(REGISTRATION_BASE_PATH)
                                .file(userData)
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Username must be between 4 and 40 characters.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_register_user_with_username_already_taken_should_return_error_response() {

        UserRegistrationDTO userRegistrationDTO = (UserRegistrationDTO) userTestBuilder.withUsername("user")
                .build(ObjectType.CREATE_DTO);

        String userAsString = ObjectTestMapper.asJsonString(userRegistrationDTO);

        MockMultipartFile userData = new MockMultipartFile("user", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                userAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(multipart(REGISTRATION_BASE_PATH)
                                .file(userData)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Username: '" + userRegistrationDTO.getUsername()
                                + "' is already taken.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_register_user_with_password_containing_username_should_return_error_response() {

        UserRegistrationDTO userRegistrationDTO = (UserRegistrationDTO) userTestBuilder.withUsername("usertest")
                .withPassword("Pausertest1!").withMatchingPassword("Pausertest1!").build(ObjectType.CREATE_DTO);

        String userAsString = ObjectTestMapper.asJsonString(userRegistrationDTO);

        MockMultipartFile userData = new MockMultipartFile("user", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, userAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(multipart(REGISTRATION_BASE_PATH)
                                .file(userData)
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Password contains user name: '"
                                + userRegistrationDTO.getUsername() + "'.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_register_user_with_password_containing_white_spaces_should_return_error_response() {

        UserRegistrationDTO userRegistrationDTO = (UserRegistrationDTO) userTestBuilder.withPassword("Pass Word 123 !@#")
                .withMatchingPassword("Pass Word 123 !@#").build(ObjectType.CREATE_DTO);

        String userAsString = ObjectTestMapper.asJsonString(userRegistrationDTO);

        MockMultipartFile userData = new MockMultipartFile("user", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, userAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(multipart(REGISTRATION_BASE_PATH)
                                .file(userData)
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Password contains a whitespace character.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_register_user_with_password_containing_a_sequence_of_repeating_characters_should_return_error_response() {

        UserRegistrationDTO userRegistrationDTO = (UserRegistrationDTO) userTestBuilder.withPassword("PAAA123a!@#")
                .withMatchingPassword("PAAA123a!@#").build(ObjectType.CREATE_DTO);

        String userAsString = ObjectTestMapper.asJsonString(userRegistrationDTO);

        MockMultipartFile userData = new MockMultipartFile("user", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, userAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(multipart(REGISTRATION_BASE_PATH)
                                .file(userData)
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Password contains a repetitive string: 'AAA'.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @ParameterizedTest(name = "{index}: For User password: {0}")
    @ValueSource(strings = {"123456", "qwerty", "iloveyou"})
    void when_register_user_with_popular_password_should_return_error_response(String popularPassword) {

        UserRegistrationDTO userRegistrationDTO = (UserRegistrationDTO) userTestBuilder.withPassword(popularPassword)
                .withMatchingPassword(popularPassword).build(ObjectType.CREATE_DTO);

        String userAsString = ObjectTestMapper.asJsonString(userRegistrationDTO);

        MockMultipartFile userData = new MockMultipartFile("user", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, userAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(multipart(REGISTRATION_BASE_PATH)
                                .file(userData)
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors", hasItem("Password contains a popular phrase: '"
                                + popularPassword + "'."))));
    }

    @Test
    void when_register_user_with_too_short_password_should_return_error_response() {

        UserRegistrationDTO userRegistrationDTO = (UserRegistrationDTO) userTestBuilder.withPassword("Pa1!")
                .withMatchingPassword("Pa1!").build(ObjectType.CREATE_DTO);

        String userAsString = ObjectTestMapper.asJsonString(userRegistrationDTO);

        MockMultipartFile userData = new MockMultipartFile("user", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, userAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(multipart(REGISTRATION_BASE_PATH)
                                .file(userData)
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Password must be 6 or more characters in length.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_register_user_with_too_long_password_should_return_error_response() {

        UserRegistrationDTO userRegistrationDTO = (UserRegistrationDTO) userTestBuilder
                .withPassword("Pa1!Pa1!Pa1!Pa1!Pa1!Pa1!Pa1!Pa1!").withMatchingPassword("Pa1!Pa1!Pa1!Pa1!Pa1!Pa1!Pa1!Pa1!")
                .build(ObjectType.CREATE_DTO);

        String userAsString = ObjectTestMapper.asJsonString(userRegistrationDTO);

        MockMultipartFile userData = new MockMultipartFile("user", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, userAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(multipart(REGISTRATION_BASE_PATH)
                                .file(userData)
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]",
                                is("Password must be no more than 30 characters in length.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_register_user_with_lowercase_password_that_does_not_meet_at_least_two_requirements_should_return_error_response() {

        UserRegistrationDTO userRegistrationDTO = (UserRegistrationDTO) userTestBuilder.withPassword("zdcsdfrg")
                .withMatchingPassword("zdcsdfrg").build(ObjectType.CREATE_DTO);

        String userAsString = ObjectTestMapper.asJsonString(userRegistrationDTO);

        MockMultipartFile userData = new MockMultipartFile("user", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, userAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(multipart(REGISTRATION_BASE_PATH)
                                .file(userData)
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
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
    void when_register_user_with_uppercase_password_that_does_not_meet_at_least_two_requirements_should_return_error_response() {

        UserRegistrationDTO userRegistrationDTO = (UserRegistrationDTO) userTestBuilder.withPassword("ONLYUPPERCASELETTERS")
                .withMatchingPassword("ONLYUPPERCASELETTERS").build(ObjectType.CREATE_DTO);

        String userAsString = ObjectTestMapper.asJsonString(userRegistrationDTO);

        MockMultipartFile userData = new MockMultipartFile("user", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, userAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(multipart(REGISTRATION_BASE_PATH)
                                .file(userData)
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
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
    void when_register_user_with_blank_email_should_return_error_response() {

        UserRegistrationDTO userRegistrationDTO = (UserRegistrationDTO) userTestBuilder.withEmail("     ")
                .build(ObjectType.CREATE_DTO);

        String userAsString = ObjectTestMapper.asJsonString(userRegistrationDTO);

        MockMultipartFile userData = new MockMultipartFile("user", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, userAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(multipart(REGISTRATION_BASE_PATH)
                                .file(userData)
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
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
    void when_register_user_with_an_incorrect_format_email_should_return_error_response(String invalidEmail) {

        UserRegistrationDTO userRegistrationDTO = (UserRegistrationDTO) userTestBuilder.withEmail(invalidEmail)
                .build(ObjectType.CREATE_DTO);

        String userAsString = ObjectTestMapper.asJsonString(userRegistrationDTO);

        MockMultipartFile userData = new MockMultipartFile("user", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, userAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(multipart(REGISTRATION_BASE_PATH)
                                .file(userData)
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Email must be a valid email address.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_register_user_with_email_already_taken_should_return_error_response() {

        UserRegistrationDTO userRegistrationDTO = (UserRegistrationDTO) userTestBuilder.withEmail("user@email.com")
                .build(ObjectType.CREATE_DTO);

        String userAsString = ObjectTestMapper.asJsonString(userRegistrationDTO);

        MockMultipartFile userData = new MockMultipartFile("user", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, userAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(multipart(REGISTRATION_BASE_PATH)
                                .file(userData)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Email: '" + userRegistrationDTO.getEmail()
                                + "' is already taken.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @ParameterizedTest(name = "{index}: For User password: {0}")
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void when_register_user_with_blank_password_should_return_error_response(String invalidPassword) {

        UserRegistrationDTO userRegistrationDTO = (UserRegistrationDTO) userTestBuilder.withPassword(invalidPassword)
                .build(ObjectType.CREATE_DTO);

        String userAsString = ObjectTestMapper.asJsonString(userRegistrationDTO);

        MockMultipartFile userData = new MockMultipartFile("user", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, userAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(multipart(REGISTRATION_BASE_PATH)
                                .file(userData)
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
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
    void when_register_user_with_blank_matching_password_should_return_error_response(String invalidMatchingPassword) {

        UserRegistrationDTO userRegistrationDTO = (UserRegistrationDTO) userTestBuilder.withMatchingPassword
                (invalidMatchingPassword).build(ObjectType.CREATE_DTO);

        String userAsString = ObjectTestMapper.asJsonString(userRegistrationDTO);

        MockMultipartFile userData = new MockMultipartFile("user", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, userAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(multipart(REGISTRATION_BASE_PATH)
                                .file(userData)
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Matching password cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_register_user_with_not_matching_passwords_should_return_error_response() {

        UserRegistrationDTO userRegistrationDTO = (UserRegistrationDTO) userTestBuilder.withPassword("Password123!@#")
                .withMatchingPassword("#@!321drowssaP").build(ObjectType.CREATE_DTO);

        String userAsString = ObjectTestMapper.asJsonString(userRegistrationDTO);

        MockMultipartFile userData = new MockMultipartFile("user", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, userAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(multipart(REGISTRATION_BASE_PATH)
                                .file(userData)
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Passwords don't match.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_register_user_with_incorrect_gender_should_return_error_response() {

        UserProfileDTO userProfileDTO = (UserProfileDTO) userProfileTestBuilder.withGender("invalid gender")
                .build(ObjectType.CREATE_DTO);
        UserRegistrationDTO userRegistrationDTO = (UserRegistrationDTO) userTestBuilder.withProfile(userProfileDTO)
                .build(ObjectType.CREATE_DTO);

        String userAsString = ObjectTestMapper.asJsonString(userRegistrationDTO);

        MockMultipartFile userData = new MockMultipartFile("user", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, userAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(multipart(REGISTRATION_BASE_PATH)
                                .file(userData)
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]",
                                is("Incorrect gender entered. The options available are: MALE, FEMALE, and UNSPECIFIED.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_register_user_with_too_long_about_section_should_return_error_response() {

        UserProfileDTO userProfileDTO = (UserProfileDTO) userProfileTestBuilder.withAbout("a".repeat(251))
                .build(ObjectType.CREATE_DTO);
        UserRegistrationDTO userRegistrationDTO = (UserRegistrationDTO) userTestBuilder.withProfile(userProfileDTO)
                .build(ObjectType.CREATE_DTO);

        String userAsString = ObjectTestMapper.asJsonString(userRegistrationDTO);

        MockMultipartFile userData = new MockMultipartFile("user", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, userAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(multipart(REGISTRATION_BASE_PATH)
                                .file(userData)
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("About section cannot be longer than 250.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_register_user_with_too_long_interests_section_should_return_error_response() {

        UserProfileDTO userProfileDTO = (UserProfileDTO) userProfileTestBuilder.withInterests("a".repeat(251))
                .build(ObjectType.CREATE_DTO);
        UserRegistrationDTO userRegistrationDTO = (UserRegistrationDTO) userTestBuilder.withProfile(userProfileDTO)
                .build(ObjectType.CREATE_DTO);

        String userAsString = ObjectTestMapper.asJsonString(userRegistrationDTO);

        MockMultipartFile userData = new MockMultipartFile("user", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, userAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(multipart(REGISTRATION_BASE_PATH)
                                .file(userData)
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Interest section cannot be longer than 250.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_register_user_with_too_long_languages_section_should_return_error_response() {

        UserProfileDTO userProfileDTO = (UserProfileDTO) userProfileTestBuilder.withLanguages("a".repeat(251))
                .build(ObjectType.CREATE_DTO);
        UserRegistrationDTO userRegistrationDTO = (UserRegistrationDTO) userTestBuilder.withProfile(userProfileDTO)
                .build(ObjectType.CREATE_DTO);

        String userAsString = ObjectTestMapper.asJsonString(userRegistrationDTO);

        MockMultipartFile userData = new MockMultipartFile("user", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, userAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(multipart(REGISTRATION_BASE_PATH)
                                .file(userData)
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Languages section cannot be longer than 250.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_register_user_with_too_long_location_section_should_return_error_response() {

        UserProfileDTO userProfileDTO = (UserProfileDTO) userProfileTestBuilder.withLocation("a".repeat(51))
                .build(ObjectType.CREATE_DTO);
        UserRegistrationDTO userRegistrationDTO = (UserRegistrationDTO) userTestBuilder.withProfile(userProfileDTO)
                .build(ObjectType.CREATE_DTO);

        String userAsString = ObjectTestMapper.asJsonString(userRegistrationDTO);

        MockMultipartFile userData = new MockMultipartFile("user", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, userAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(multipart(REGISTRATION_BASE_PATH)
                                .file(userData)
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Location cannot be longer than 50.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }
}
