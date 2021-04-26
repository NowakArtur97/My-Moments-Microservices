package com.nowakArtur97.myMoments.userService.domain.resource;

import com.nowakArtur97.myMoments.userService.common.util.JwtUtil;
import com.nowakArtur97.myMoments.userService.domain.document.*;
import com.nowakArtur97.myMoments.userService.domain.testBuilder.UserProfileTestBuilder;
import com.nowakArtur97.myMoments.userService.domain.testBuilder.UserTestBuilder;
import com.nowakArtur97.myMoments.userService.exception.RoleNotFoundException;
import com.nowakArtur97.myMoments.userService.testUtil.enums.ObjectType;
import com.nowakArtur97.myMoments.userService.testUtil.generator.NameWithSpacesGenerator;
import com.nowakArtur97.myMoments.userService.testUtil.mapper.ObjectTestMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("UserController_Tests")
class UserUpdateControllerTest {

    @LocalServerPort
    private int serverPort;

    private final String USER_BASE_PATH = "http://localhost:" + serverPort + "/api/v1/users/me";
    
    @Value("${my-moments.jwt.validity:36000000}")
    private Long validity;

    @Value("${my-moments.default-user-role:USER_ROLE}")
    private String defaultUserRole;

    private MockMultipartHttpServletRequestBuilder mockRequestBuilder;

    @Autowired
    private MockMvc mockMvc;

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
    private UserProfileDocument userProfileDocument;
    private UserDocument userDocument;
    private RoleDocument roleDocument;

    @BeforeAll
    static void setUpBuilders() {

        userProfileTestBuilder = new UserProfileTestBuilder();
        userTestBuilder = new UserTestBuilder();
    }

    @BeforeEach
    void setUp() {

        roleDocument = roleRepository.findByName(defaultUserRole)
                .orElseThrow(() -> new RoleNotFoundException("Role: '" + defaultUserRole + "' not found."));

        userProfileDocument = (UserProfileDocument) userProfileTestBuilder.build(ObjectType.DOCUMENT);
        userDocument = (UserDocument) userTestBuilder.withUsername("user456").withEmail("u4se5r@em6ail.com")
                .withPassword("userASD!").withMatchingPassword("userASD!").withProfile(userProfileDocument)
                .withRoles(Set.of(roleDocument)).build(ObjectType.DOCUMENT);

        userRepository.save(userDocument);

        token = jwtUtil.generateToken(new User(userDocument.getUsername(), userDocument.getPassword(),
                List.of(new SimpleGrantedAuthority(defaultUserRole))));

        mockRequestBuilder = MockMvcRequestBuilders.multipart(USER_BASE_PATH, userDocument.getId());
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
    void when_update_valid_user_without_profile_should_update_user() {

        UserUpdateDTO userUpdateDTO = (UserUpdateDTO) userTestBuilder.withUsername("validUser")
                .withEmail("validUser123@email.com").withPassword("ValidPassword123!").withMatchingPassword("ValidPassword123!")
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
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("username", is(userUpdateDTO.getUsername())))
                        .andExpect(jsonPath("email", is(userUpdateDTO.getEmail())))
                        .andExpect(jsonPath("password").doesNotExist())
                        .andExpect(jsonPath("authenticationResponse.token", notNullValue()))
                        .andExpect(jsonPath("authenticationResponse.expirationTimeInMilliseconds",
                                is(validity.intValue())))
                        .andExpect(jsonPath("profile.about", is(userProfileDocument.getAbout())))
                        .andExpect(jsonPath("profile.gender", is(userProfileDocument.getGender().toString())))
                        .andExpect(jsonPath("profile.interests", is(userProfileDocument.getInterests())))
                        .andExpect(jsonPath("profile.languages", is(userProfileDocument.getLanguages())))
                        .andExpect(jsonPath("profile.location", is(userProfileDocument.getLocation())))
                        .andExpect(jsonPath("profile.image").doesNotExist())
                        .andExpect(jsonPath("roles[0].name", is(roleDocument.getName())))
        );
    }

    @Test
    void when_update_valid_user_without_changing_username_should_update_user() {

        UserUpdateDTO userUpdateDTO = (UserUpdateDTO) userTestBuilder.withUsername(userDocument.getUsername())
                .withEmail("validUser123@email.com").withPassword("ValidPassword123!").withMatchingPassword("ValidPassword123!")
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
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("username", is(userDocument.getUsername())))
                        .andExpect(jsonPath("email", is(userUpdateDTO.getEmail())))
                        .andExpect(jsonPath("password").doesNotExist())
                        .andExpect(jsonPath("authenticationResponse.token", notNullValue()))
                        .andExpect(jsonPath("authenticationResponse.expirationTimeInMilliseconds",
                                is(validity.intValue())))
                        .andExpect(jsonPath("profile.about", is(userProfileDocument.getAbout())))
                        .andExpect(jsonPath("profile.gender", is(userProfileDocument.getGender().toString())))
                        .andExpect(jsonPath("profile.interests", is(userProfileDocument.getInterests())))
                        .andExpect(jsonPath("profile.languages", is(userProfileDocument.getLanguages())))
                        .andExpect(jsonPath("profile.location", is(userProfileDocument.getLocation())))
                        .andExpect(jsonPath("profile.image").doesNotExist())
                        .andExpect(jsonPath("roles[0].name", is(roleDocument.getName())))
        );
    }

    @Test
    void when_update_valid_user_without_changing_email_should_update_user() {

        UserUpdateDTO userUpdateDTO = (UserUpdateDTO) userTestBuilder.withUsername("validUser")
                .withEmail(userDocument.getEmail()).withPassword("ValidPassword123!").withMatchingPassword("ValidPassword123!")
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
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("username", is(userUpdateDTO.getUsername())))
                        .andExpect(jsonPath("email", is(userDocument.getEmail())))
                        .andExpect(jsonPath("password").doesNotExist())
                        .andExpect(jsonPath("authenticationResponse.token", notNullValue()))
                        .andExpect(jsonPath("authenticationResponse.expirationTimeInMilliseconds",
                                is(validity.intValue())))
                        .andExpect(jsonPath("profile.about", is(userProfileDocument.getAbout())))
                        .andExpect(jsonPath("profile.gender", is(userProfileDocument.getGender().toString())))
                        .andExpect(jsonPath("profile.interests", is(userProfileDocument.getInterests())))
                        .andExpect(jsonPath("profile.languages", is(userProfileDocument.getLanguages())))
                        .andExpect(jsonPath("profile.location", is(userProfileDocument.getLocation())))
                        .andExpect(jsonPath("profile.image").doesNotExist())
                        .andExpect(jsonPath("roles[0].name", is(roleDocument.getName())))
        );
    }

    @Test
    void when_update_valid_user_with_profile_should_update_user() {

        UserProfileDTO userProfileDTO = (UserProfileDTO) userProfileTestBuilder.withAbout("new about")
                .withInterests("new interests").withLanguages("new languages").withLocation("new location")
                .withGender(Gender.FEMALE).build(ObjectType.UPDATE_DTO);
        UserUpdateDTO userUpdateDTO = (UserUpdateDTO) userTestBuilder.withUsername("validUserWithProfile")
                .withEmail("validUser123Profile@email.com").withPassword("ValidPassword123!")
                .withMatchingPassword("ValidPassword123!").withProfile(userProfileDTO)
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
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("username", is(userUpdateDTO.getUsername())))
                        .andExpect(jsonPath("email", is(userUpdateDTO.getEmail())))
                        .andExpect(jsonPath("password").doesNotExist())
                        .andExpect(jsonPath("authenticationResponse.token", notNullValue()))
                        .andExpect(jsonPath("authenticationResponse.expirationTimeInMilliseconds",
                                is(validity.intValue())))
                        .andExpect(jsonPath("profile.about", is(userProfileDTO.getAbout())))
                        .andExpect(jsonPath("profile.gender", is(userProfileDTO.getGender())))
                        .andExpect(jsonPath("profile.interests", is(userProfileDTO.getInterests())))
                        .andExpect(jsonPath("profile.languages", is(userProfileDTO.getLanguages())))
                        .andExpect(jsonPath("profile.location", is(userProfileDTO.getLocation())))
                        .andExpect(jsonPath("profile.image").doesNotExist())
                        .andExpect(jsonPath("roles[0].name", is(roleDocument.getName())))
        );
    }

    @Test
    void when_update_valid_user_with_null_profile_fields_should_update_user() {

        UserProfileDTO userProfileDTO = (UserProfileDTO) userProfileTestBuilder.withAbout(null).withInterests(null)
                .withLanguages(null).withLocation(null).build(ObjectType.UPDATE_DTO);
        UserUpdateDTO userUpdateDTO = (UserUpdateDTO) userTestBuilder.withUsername("validUserWithProfile")
                .withEmail("validUser123Profile@email.com").withPassword("ValidPassword123!")
                .withMatchingPassword("ValidPassword123!").withProfile(userProfileDTO)
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
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("username", is(userUpdateDTO.getUsername())))
                        .andExpect(jsonPath("email", is(userUpdateDTO.getEmail())))
                        .andExpect(jsonPath("password").doesNotExist())
                        .andExpect(jsonPath("authenticationResponse.token", notNullValue()))
                        .andExpect(jsonPath("authenticationResponse.expirationTimeInMilliseconds",
                                is(validity.intValue())))
                        .andExpect(jsonPath("profile.about", is("")))
                        .andExpect(jsonPath("profile.gender", is(Gender.UNSPECIFIED.toString())))
                        .andExpect(jsonPath("profile.interests", is("")))
                        .andExpect(jsonPath("profile.languages", is("")))
                        .andExpect(jsonPath("profile.location", is("")))
                        .andExpect(jsonPath("profile.image").doesNotExist())
                        .andExpect(jsonPath("roles[0].name", is(roleDocument.getName())))
        );
    }

    @Test
    void when_update_valid_user_with_empty_profile_fields_should_update_user() {

        UserProfileDTO userProfileDTO = (UserProfileDTO) userProfileTestBuilder.withAbout("").withInterests("")
                .withLanguages("").withLocation("").build(ObjectType.UPDATE_DTO);
        UserUpdateDTO userUpdateDTO = (UserUpdateDTO) userTestBuilder.withUsername("validUserWithProfile")
                .withEmail("validUser123Profile@email.com").withPassword("ValidPassword123!")
                .withMatchingPassword("ValidPassword123!").withProfile(userProfileDTO)
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
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("username", is(userUpdateDTO.getUsername())))
                        .andExpect(jsonPath("email", is(userUpdateDTO.getEmail())))
                        .andExpect(jsonPath("password").doesNotExist())
                        .andExpect(jsonPath("authenticationResponse.token", notNullValue()))
                        .andExpect(jsonPath("authenticationResponse.expirationTimeInMilliseconds",
                                is(validity.intValue())))
                        .andExpect(jsonPath("profile.about", is("")))
                        .andExpect(jsonPath("profile.gender", is(Gender.UNSPECIFIED.toString())))
                        .andExpect(jsonPath("profile.interests", is("")))
                        .andExpect(jsonPath("profile.languages", is("")))
                        .andExpect(jsonPath("profile.location", is("")))
                        .andExpect(jsonPath("profile.image").doesNotExist())
                        .andExpect(jsonPath("roles[0].name", is(roleDocument.getName())))
        );
    }

    @Test
    void when_update_valid_user_with_profile_and_image_should_update_user() {

        UserProfileDTO userProfileDTO = (UserProfileDTO) userProfileTestBuilder.withAbout("new about 2")
                .withInterests("new interests 2").withLanguages("new languages 2").withLocation("new location 2")
                .withGender(Gender.FEMALE).build(ObjectType.UPDATE_DTO);
        UserUpdateDTO userUpdateDTO = (UserUpdateDTO) userTestBuilder.withUsername("validUserWithImage2")
                .withEmail("validUser123Image2@email.com").withPassword("ValidPassword123!")
                .withMatchingPassword("ValidPassword123!").withProfile(userProfileDTO)
                .build(ObjectType.UPDATE_DTO);

        String userAsString = ObjectTestMapper.asJsonString(userUpdateDTO);

        MockMultipartFile userData = new MockMultipartFile("user", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, userAsString.getBytes(StandardCharsets.UTF_8));

        MockMultipartFile image = new MockMultipartFile("image", "image", MediaType.IMAGE_JPEG_VALUE,
                "image.jpg".getBytes());

        assertAll(
                () -> mockMvc
                        .perform(mockRequestBuilder
                                .file(userData)
                                .file(image)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("username", is(userUpdateDTO.getUsername())))
                        .andExpect(jsonPath("email", is(userUpdateDTO.getEmail())))
                        .andExpect(jsonPath("password").doesNotExist())
                        .andExpect(jsonPath("authenticationResponse.token", notNullValue()))
                        .andExpect(jsonPath("authenticationResponse.expirationTimeInMilliseconds",
                                is(validity.intValue())))
                        .andExpect(jsonPath("profile.about", is(userProfileDTO.getAbout())))
                        .andExpect(jsonPath("profile.gender", is(userProfileDTO.getGender())))
                        .andExpect(jsonPath("profile.interests", is(userProfileDTO.getInterests())))
                        .andExpect(jsonPath("profile.languages", is(userProfileDTO.getLanguages())))
                        .andExpect(jsonPath("profile.location", is(userProfileDTO.getLocation())))
                        .andExpect(jsonPath("profile.image").doesNotExist())
                        .andExpect(jsonPath("roles[0].name", is(roleDocument.getName())))
        );
    }
}