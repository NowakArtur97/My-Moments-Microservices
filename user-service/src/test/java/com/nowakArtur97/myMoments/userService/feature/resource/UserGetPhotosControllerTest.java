package com.nowakArtur97.myMoments.userService.feature.resource;

import com.nowakArtur97.myMoments.userService.common.util.JwtUtil;
import com.nowakArtur97.myMoments.userService.exception.RoleNotFoundException;
import com.nowakArtur97.myMoments.userService.feature.document.*;
import com.nowakArtur97.myMoments.userService.feature.testBuilder.UserProfileTestBuilder;
import com.nowakArtur97.myMoments.userService.feature.testBuilder.UserTestBuilder;
import com.nowakArtur97.myMoments.userService.testUtil.enums.ObjectType;
import com.nowakArtur97.myMoments.userService.testUtil.generator.NameWithSpacesGenerator;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("UserController_Tests")
class UserGetPhotosControllerTest {

    @LocalServerPort
    private int serverPort;

    private final String GET_PHOTOS_BASE_PATH = "http://localhost:" + serverPort + "/api/v1/users/photos";

    private final String USERNAMES_REQUEST_PARAMETER = "usernames";

    @Value("${my-moments.default-user-role:USER_ROLE}")
    private String defaultUserRole;

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

        UserProfileDocument userProfileDocument = (UserProfileDocument) userProfileTestBuilder
                .withImage("image.jpg".getBytes()).build(ObjectType.DOCUMENT);
        UserDocument userDocument = (UserDocument) userTestBuilder.withUsername("user4567")
                .withEmail("u4se5r7@em6ail.com").withPassword("userASD!").withMatchingPassword("userASD!")
                .withProfile(userProfileDocument).withRoles(Set.of(roleDocument))
                .build(ObjectType.DOCUMENT);

        userRepository.save(userDocument);

        token = jwtUtil.generateToken(new User(userDocument.getUsername(), userDocument.getPassword(),
                List.of(new SimpleGrantedAuthority(defaultUserRole))));
    }

    @AfterEach
    void cleanUpDatabase() {

        mongoTemplate.getDb().drop();
    }

    @Test
    void when_get_existing_user_photo_should_return_photo() {

        MultiValueMap<String, String> expectedUsernames = new LinkedMultiValueMap<>();
        expectedUsernames.addAll(USERNAMES_REQUEST_PARAMETER, List.of("user4567"));

        assertAll(
                () -> mockMvc.perform(
                                get(GET_PHOTOS_BASE_PATH)
                                        .params(expectedUsernames)
                                        .header("Authorization", "Bearer " + token)
                                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$", hasSize(1)))
                        .andExpect(jsonPath("$.[0]").isNotEmpty())
        );
    }

    @Test
    void when_get_existing_users_photos_should_return_photos() {

        UserProfileDocument userProfileDocument = (UserProfileDocument) userProfileTestBuilder
                .withImage("image2.jpg".getBytes()).build(ObjectType.DOCUMENT);
        UserDocument userDocument = (UserDocument) userTestBuilder.withUsername("user4567_2")
                .withEmail("u4se5r_2@em6ail.com").withPassword("userASD!").withMatchingPassword("userASD!")
                .withProfile(userProfileDocument).withRoles(Set.of(roleDocument))
                .build(ObjectType.DOCUMENT);
        UserProfileDocument userProfileDocument2 = (UserProfileDocument) userProfileTestBuilder
                .withImage("image3.jpg".getBytes()).build(ObjectType.DOCUMENT);
        UserDocument userDocument2 = (UserDocument) userTestBuilder.withUsername("user4567_3")
                .withEmail("u4se5r_3@em6ail.com").withPassword("userASD!").withMatchingPassword("userASD!")
                .withProfile(userProfileDocument2).withRoles(Set.of(roleDocument))
                .build(ObjectType.DOCUMENT);

        userRepository.saveAll(List.of(userDocument, userDocument2));

        MultiValueMap<String, String> expectedUsernames = new LinkedMultiValueMap<>();
        expectedUsernames.addAll(USERNAMES_REQUEST_PARAMETER,
                List.of(userDocument.getUsername(), userDocument2.getUsername()));

        assertAll(
                () -> mockMvc.perform(
                                get(GET_PHOTOS_BASE_PATH)
                                        .params(expectedUsernames)
                                        .header("Authorization", "Bearer " + token)
                                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$", hasSize(2)))
                        .andExpect(jsonPath("$.[0]").isNotEmpty())
                        .andExpect(jsonPath("$.[1]").isNotEmpty())
        );
    }

    @Test
    void when_get_existing_and_not_existing_users_photos_should_return_existing_user_photo() {

        UserProfileDocument userProfileDocument = (UserProfileDocument) userProfileTestBuilder
                .withImage("image2.jpg".getBytes()).build(ObjectType.DOCUMENT);
        UserDocument userDocument = (UserDocument) userTestBuilder.withUsername("user4567_2")
                .withEmail("u4se5r_2@em6ail.com").withPassword("userASD!").withMatchingPassword("userASD!")
                .withProfile(userProfileDocument).withRoles(Set.of(roleDocument))
                .build(ObjectType.DOCUMENT);

        userRepository.save(userDocument);

        MultiValueMap<String, String> expectedUsernames = new LinkedMultiValueMap<>();
        expectedUsernames.addAll(USERNAMES_REQUEST_PARAMETER, List.of(userDocument.getUsername(), "notExistingUser"));

        assertAll(
                () -> mockMvc.perform(
                                get(GET_PHOTOS_BASE_PATH)
                                        .params(expectedUsernames)
                                        .header("Authorization", "Bearer " + token)
                                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$", hasSize(1)))
                        .andExpect(jsonPath("$.[0]").isNotEmpty())
        );
    }

    @Test
    void when_get_not_existing_users_photos_should_return_empty_array() {

        MultiValueMap<String, String> expectedUsernames = new LinkedMultiValueMap<>();
        expectedUsernames.addAll(USERNAMES_REQUEST_PARAMETER, List.of("notExistingUser", "notExistingUser2"));

        assertAll(
                () -> mockMvc.perform(
                                get(GET_PHOTOS_BASE_PATH)
                                        .params(expectedUsernames)
                                        .header("Authorization", "Bearer " + token)
                                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$").isEmpty())
        );
    }

    @Test
    void when_get_users_photos_without_request_param_should_return_bad_request_response() {

        assertAll(
                () -> mockMvc.perform(
                                get(GET_PHOTOS_BASE_PATH)
                                        .header("Authorization", "Bearer " + token)
                                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
        );
    }
}
