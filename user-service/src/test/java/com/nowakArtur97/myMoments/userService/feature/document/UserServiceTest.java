package com.nowakArtur97.myMoments.userService.feature.document;

import com.nowakArtur97.myMoments.userService.exception.ResourceNotFoundException;
import com.nowakArtur97.myMoments.userService.feature.messaging.UserEventProducer;
import com.nowakArtur97.myMoments.userService.feature.messaging.UserUpdateEventPayload;
import com.nowakArtur97.myMoments.userService.feature.resource.UserPhotoModel;
import com.nowakArtur97.myMoments.userService.feature.resource.UserProfileDTO;
import com.nowakArtur97.myMoments.userService.feature.resource.UserRegistrationDTO;
import com.nowakArtur97.myMoments.userService.feature.resource.UserUpdateDTO;
import com.nowakArtur97.myMoments.userService.feature.testBuilder.UserProfileTestBuilder;
import com.nowakArtur97.myMoments.userService.feature.testBuilder.UserTestBuilder;
import com.nowakArtur97.myMoments.userService.testUtil.enums.ObjectType;
import com.nowakArtur97.myMoments.userService.testUtil.generator.NameWithSpacesGenerator;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("UserService_Tests")
class UserServiceTest {

    private final String defaultUserRole = "USER_ROLE";

    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private RoleService roleService;

    @Mock
    private UserEventProducer userEventProducer;

    private static MockedStatic<UUID> mocked;

    private static UserProfileTestBuilder userProfileTestBuilder;
    private static UserTestBuilder userTestBuilder;

    @BeforeAll
    static void setUpBuildersAndUUID() {

        userProfileTestBuilder = new UserProfileTestBuilder();
        userTestBuilder = new UserTestBuilder();

        UUID uuid = UUID.randomUUID();
        mocked = mockStatic(UUID.class);
        mocked.when(UUID::randomUUID).thenReturn(uuid);
    }

    @BeforeEach
    void setUp() {

        userService = new UserService(userRepository, roleService, userMapper, userEventProducer);

        ReflectionTestUtils.setField(userService, "defaultUserRole", "USER_ROLE");
    }

    @AfterAll
    static void cleanUp() {

        if (!mocked.isClosed()) {
            mocked.close();
        }
    }

    @Nested
    class RegisterUserTest {

        @Test
        @SneakyThrows
        void when_register_user_with_profile_should_register_user() {

            UserProfileDTO userProfileDTOExpected = (UserProfileDTO) userProfileTestBuilder.build(ObjectType.CREATE_DTO);
            UserRegistrationDTO userRegistrationDTOExpected = (UserRegistrationDTO) userTestBuilder
                    .withProfile(userProfileDTOExpected).build(ObjectType.CREATE_DTO);

            MockMultipartFile image = new MockMultipartFile("image", "image", "application/json",
                    "image.jpg".getBytes());
            UserProfileDocument userProfileExpectedAfterObjectMapping = (UserProfileDocument) userProfileTestBuilder
                    .withImage(image.getBytes()).build(ObjectType.DOCUMENT);
            UserDocument userExpectedAfterObjectMapping = (UserDocument) userTestBuilder
                    .withProfile(userProfileExpectedAfterObjectMapping).build(ObjectType.DOCUMENT);
            RoleDocument roleExpected = new RoleDocument(defaultUserRole);
            String passwordEncoded = "encodedPassword";
            UserDocument userExpectedAfterPasswordEncodingAndSettingRoles = (UserDocument) userTestBuilder
                    .withPassword(passwordEncoded)
                    .withProfile(userProfileExpectedAfterObjectMapping).withRoles(Set.of(roleExpected))
                    .build(ObjectType.DOCUMENT);
            UserDocument userExpected = (UserDocument) userTestBuilder.withPassword(passwordEncoded)
                    .withProfile(userProfileExpectedAfterObjectMapping).withRoles(Set.of(roleExpected))
                    .build(ObjectType.DOCUMENT);

            when(userMapper.convertDTOToDocument(userRegistrationDTOExpected, image, roleExpected))
                    .thenReturn(userExpectedAfterObjectMapping);
            when(roleService.findByName(defaultUserRole)).thenReturn(Optional.of(roleExpected));
            when(userRepository.save(userExpectedAfterPasswordEncodingAndSettingRoles)).thenReturn(userExpected);

            UserDocument userActual = userService.registerUser(userRegistrationDTOExpected, image);

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
                    () -> assertEquals(userExpected.getProfile(), userActual.getProfile(),
                            () -> "should return user with profile: " + userExpected.getProfile()
                                    + ", but was" + userActual.getProfile()),
                    () -> assertEquals(userExpected.getProfile().getAbout(), userActual.getProfile().getAbout(),
                            () -> "should return user with about section: " + userExpected.getProfile().getAbout()
                                    + ", but was" + userActual.getProfile().getAbout()),
                    () -> assertEquals(userExpected.getProfile().getGender(), userActual.getProfile().getGender(),
                            () -> "should return user with gender: " + userExpected.getProfile().getGender()
                                    + ", but was" + userActual.getProfile().getGender()),
                    () -> assertEquals(userExpected.getProfile().getInterests(), userActual.getProfile().getInterests(),
                            () -> "should return user with interests section: " + userExpected.getProfile().getInterests()
                                    + ", but was" + userActual.getProfile().getInterests()),
                    () -> assertEquals(userExpected.getProfile().getLanguages(), userActual.getProfile().getLanguages(),
                            () -> "should return user with languages section: " + userExpected.getProfile().getLanguages()
                                    + ", but was" + userActual.getProfile().getLanguages()),
                    () -> assertEquals(userExpected.getProfile().getLocation(), userActual.getProfile().getLocation(),
                            () -> "should return user with location: " + userExpected.getProfile().getLocation()
                                    + ", but was" + userActual.getProfile().getLocation()),
                    () -> assertEquals(userExpected.getProfile().getImage(), userActual.getProfile().getImage(),
                            () -> "should return user with image: " + userExpected.getProfile().getImage()
                                    + ", but was" + userActual.getProfile().getImage()),
                    () -> verify(userMapper, times(1))
                            .convertDTOToDocument(userRegistrationDTOExpected, image, roleExpected),
                    () -> verifyNoMoreInteractions(userMapper),
                    () -> verify(roleService, times(1)).findByName(defaultUserRole),
                    () -> verifyNoMoreInteractions(roleService),
                    () -> verify(userRepository, times(1)).save(userExpectedAfterObjectMapping),
                    () -> verifyNoMoreInteractions(userRepository),
                    () -> verifyNoInteractions(userEventProducer));
        }

        @Test
        @SneakyThrows
        void when_register_user_without_profile_should_register_user() {

            UserRegistrationDTO userRegistrationDTOExpected = (UserRegistrationDTO) userTestBuilder
                    .build(ObjectType.CREATE_DTO);

            MockMultipartFile image = new MockMultipartFile("image", "image", "application/json",
                    "image.jpg".getBytes());
            UserProfileDocument userProfileExpectedAfterObjectMapping = (UserProfileDocument) userProfileTestBuilder
                    .withAbout("").withInterests("").withLanguages("").withLocation("").withImage(image.getBytes())
                    .build(ObjectType.DOCUMENT);
            UserDocument userExpectedAfterObjectMapping = (UserDocument) userTestBuilder
                    .withProfile(userProfileExpectedAfterObjectMapping).build(ObjectType.DOCUMENT);
            RoleDocument roleExpected = new RoleDocument(defaultUserRole);
            String passwordEncoded = "encodedPassword";
            UserDocument userExpectedAfterPasswordEncodingAndSettingRoles = (UserDocument) userTestBuilder
                    .withPassword(passwordEncoded).withProfile(userProfileExpectedAfterObjectMapping)
                    .withRoles(Set.of(roleExpected)).build(ObjectType.DOCUMENT);
            UserDocument userExpected = (UserDocument) userTestBuilder.withPassword(passwordEncoded)
                    .withProfile(userProfileExpectedAfterObjectMapping).withRoles(Set.of(roleExpected))
                    .build(ObjectType.DOCUMENT);

            when(userMapper.convertDTOToDocument(userRegistrationDTOExpected, image, roleExpected))
                    .thenReturn(userExpectedAfterObjectMapping);
            when(roleService.findByName(defaultUserRole)).thenReturn(Optional.of(roleExpected));
            when(userRepository.save(userExpectedAfterPasswordEncodingAndSettingRoles)).thenReturn(userExpected);

            UserDocument userActual = userService.registerUser(userRegistrationDTOExpected, image);

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
                    () -> assertEquals(userExpected.getProfile(), userActual.getProfile(),
                            () -> "should return user with profile: " + userExpected.getProfile()
                                    + ", but was" + userActual.getProfile()),
                    () -> assertEquals(userExpected.getProfile().getAbout(), userActual.getProfile().getAbout(),
                            () -> "should return user with about section: " + userExpected.getProfile().getAbout()
                                    + ", but was" + userActual.getProfile().getAbout()),
                    () -> assertEquals(userExpected.getProfile().getGender(), userActual.getProfile().getGender(),
                            () -> "should return user with gender: " + userExpected.getProfile().getGender()
                                    + ", but was" + userActual.getProfile().getGender()),
                    () -> assertEquals(userExpected.getProfile().getInterests(), userActual.getProfile().getInterests(),
                            () -> "should return user with interests section: " + userExpected.getProfile().getInterests()
                                    + ", but was" + userActual.getProfile().getInterests()),
                    () -> assertEquals(userExpected.getProfile().getLanguages(), userActual.getProfile().getLanguages(),
                            () -> "should return user with languages section: " + userExpected.getProfile().getLanguages()
                                    + ", but was" + userActual.getProfile().getLanguages()),
                    () -> assertEquals(userExpected.getProfile().getLocation(), userActual.getProfile().getLocation(),
                            () -> "should return user with location: " + userExpected.getProfile().getLocation()
                                    + ", but was" + userActual.getProfile().getLocation()),
                    () -> assertEquals(userExpected.getProfile().getImage(), userActual.getProfile().getImage(),
                            () -> "should return user with image: " + userExpected.getProfile().getImage()
                                    + ", but was" + userActual.getProfile().getImage()),
                    () -> verify(userMapper, times(1))
                            .convertDTOToDocument(userRegistrationDTOExpected, image, roleExpected),
                    () -> verifyNoMoreInteractions(userMapper),
                    () -> verify(roleService, times(1)).findByName(defaultUserRole),
                    () -> verifyNoMoreInteractions(roleService),
                    () -> verify(userRepository, times(1)).save(userExpectedAfterObjectMapping),
                    () -> verifyNoMoreInteractions(userRepository),
                    () -> verifyNoInteractions(userEventProducer));
        }

        @Test
        @SneakyThrows
        void when_register_user_without_profile_and_image_should_register_user() {

            UserRegistrationDTO userRegistrationDTOExpected = (UserRegistrationDTO) userTestBuilder
                    .build(ObjectType.CREATE_DTO);

            MockMultipartFile image = null;
            UserProfileDocument userProfileExpectedAfterObjectMapping = (UserProfileDocument) userProfileTestBuilder
                    .withAbout("").withInterests("").withLanguages("").withLocation("").withImage(null)
                    .build(ObjectType.DOCUMENT);
            UserDocument userExpectedAfterObjectMapping = (UserDocument) userTestBuilder
                    .withProfile(userProfileExpectedAfterObjectMapping).build(ObjectType.DOCUMENT);
            RoleDocument roleExpected = new RoleDocument(defaultUserRole);
            String passwordEncoded = "encodedPassword";
            UserDocument userExpectedAfterPasswordEncodingAndSettingRoles = (UserDocument) userTestBuilder
                    .withPassword(passwordEncoded).withProfile(userProfileExpectedAfterObjectMapping)
                    .withRoles(Set.of(roleExpected)).build(ObjectType.DOCUMENT);
            UserDocument userExpected = (UserDocument) userTestBuilder.withPassword(passwordEncoded)
                    .withProfile(userProfileExpectedAfterObjectMapping).withRoles(Set.of(roleExpected))
                    .build(ObjectType.DOCUMENT);

            when(userMapper.convertDTOToDocument(userRegistrationDTOExpected, image, roleExpected))
                    .thenReturn(userExpectedAfterObjectMapping);
            when(roleService.findByName(defaultUserRole)).thenReturn(Optional.of(roleExpected));
            when(userRepository.save(userExpectedAfterPasswordEncodingAndSettingRoles)).thenReturn(userExpected);

            UserDocument userActual = userService.registerUser(userRegistrationDTOExpected, image);

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
                    () -> assertEquals(userExpected.getProfile(), userActual.getProfile(),
                            () -> "should return user with profile: " + userExpected.getProfile()
                                    + ", but was" + userActual.getProfile()),
                    () -> assertEquals(userExpected.getProfile().getAbout(), userActual.getProfile().getAbout(),
                            () -> "should return user with about section: " + userExpected.getProfile().getAbout()
                                    + ", but was" + userActual.getProfile().getAbout()),
                    () -> assertEquals(userExpected.getProfile().getGender(), userActual.getProfile().getGender(),
                            () -> "should return user with gender: " + userExpected.getProfile().getGender()
                                    + ", but was" + userActual.getProfile().getGender()),
                    () -> assertEquals(userExpected.getProfile().getInterests(), userActual.getProfile().getInterests(),
                            () -> "should return user with interests section: " + userExpected.getProfile().getInterests()
                                    + ", but was" + userActual.getProfile().getInterests()),
                    () -> assertEquals(userExpected.getProfile().getLanguages(), userActual.getProfile().getLanguages(),
                            () -> "should return user with languages section: " + userExpected.getProfile().getLanguages()
                                    + ", but was" + userActual.getProfile().getLanguages()),
                    () -> assertEquals(userExpected.getProfile().getLocation(), userActual.getProfile().getLocation(),
                            () -> "should return user with location: " + userExpected.getProfile().getLocation()
                                    + ", but was" + userActual.getProfile().getLocation()),
                    () -> assertEquals(userExpected.getProfile().getImage(), userActual.getProfile().getImage(),
                            () -> "should return user with image: " + userExpected.getProfile().getImage()
                                    + ", but was" + userActual.getProfile().getImage()),
                    () -> verify(userMapper, times(1))
                            .convertDTOToDocument(userRegistrationDTOExpected, image, roleExpected),
                    () -> verifyNoMoreInteractions(userMapper),
                    () -> verify(roleService, times(1)).findByName(defaultUserRole),
                    () -> verifyNoMoreInteractions(roleService),
                    () -> verify(userRepository, times(1)).save(userExpectedAfterObjectMapping),
                    () -> verifyNoMoreInteractions(userRepository),
                    () -> verifyNoInteractions(userEventProducer));
        }
    }

    @Nested
    class UpdaterUserTest {

        @Test
        @SneakyThrows
        void when_update_user_with_profile_should_update_user() {

            UserProfileDTO userProfileDTO = (UserProfileDTO) userProfileTestBuilder.withAbout("new about")
                    .withInterests("new interests").withLanguages("new languages").withLocation("new location")
                    .withGender(Gender.FEMALE).build(ObjectType.UPDATE_DTO);
            UserUpdateDTO userUpdateDTOExpected = (UserUpdateDTO) userTestBuilder.withUsername("validUser")
                    .withEmail("validUser123@email.com").withPassword("ValidPassword123!")
                    .withMatchingPassword("ValidPassword123!").withProfile(userProfileDTO).build(ObjectType.UPDATE_DTO);

            MockMultipartFile image = new MockMultipartFile("image", "image", "application/json",
                    "image.jpg".getBytes());

            UserProfileDocument userProfileExpectedBeforeUpdate = (UserProfileDocument) userProfileTestBuilder
                    .withAbout("").withInterests("").withLanguages("").withLocation("").withImage(image.getBytes())
                    .build(ObjectType.DOCUMENT);
            UserDocument userExpectedBeforeUpdate = (UserDocument) userTestBuilder.withUsername("previous username")
                    .withEmail("prevoius@email.com").withPassword("oldPass123!").withProfile(userProfileExpectedBeforeUpdate)
                    .build(ObjectType.DOCUMENT);
            UserProfileDocument userProfileExpectedAfterObjectMapping = (UserProfileDocument) userProfileTestBuilder
                    .withAbout("").withInterests("").withLanguages("").withLocation("").withImage(image.getBytes())
                    .build(ObjectType.DOCUMENT);
            UserDocument userExpectedAfterObjectMapping = (UserDocument) userTestBuilder
                    .withProfile(userProfileExpectedAfterObjectMapping).build(ObjectType.DOCUMENT);
            RoleDocument roleExpected = new RoleDocument(defaultUserRole);
            String passwordEncoded = "encodedPassword";
            UserDocument userExpectedAfterPasswordEncodingAndSettingRoles = (UserDocument) userTestBuilder
                    .withPassword(passwordEncoded).withProfile(userProfileExpectedAfterObjectMapping)
                    .withRoles(Set.of(roleExpected)).build(ObjectType.DOCUMENT);
            UserUpdateEventPayload userUpdateEventPayload =
                    new UserUpdateEventPayload(userExpectedBeforeUpdate.getUsername(), userUpdateDTOExpected.getUsername());
            UserDocument userExpected = (UserDocument) userTestBuilder.withPassword(passwordEncoded)
                    .withProfile(userProfileExpectedAfterObjectMapping).withRoles(Set.of(roleExpected))
                    .build(ObjectType.DOCUMENT);

            when(userRepository.findByUsername(userExpectedBeforeUpdate.getUsername()))
                    .thenReturn(Optional.of(userExpectedBeforeUpdate));
            when(userRepository.save(userExpectedAfterPasswordEncodingAndSettingRoles)).thenReturn(userExpected);

            UserDocument userActual = userService.updateUser(userExpectedBeforeUpdate.getUsername(), userUpdateDTOExpected, image);

            assertAll(() -> assertEquals(userExpected, userActual,
                            () -> "should return user: " + userExpected + ", but was" + userActual),
                    () -> assertEquals(userExpected.getId(), userActual.getId(),
                            () -> "should return user with id: " + userExpected.getId() + ", but was"
                                    + userActual.getId()),
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
                    () -> assertEquals(userExpected.getProfile(), userActual.getProfile(),
                            () -> "should return user with profile: " + userExpected.getProfile()
                                    + ", but was" + userActual.getProfile()),
                    () -> assertEquals(userExpected.getProfile().getAbout(), userActual.getProfile().getAbout(),
                            () -> "should return user with about section: " + userExpected.getProfile().getAbout()
                                    + ", but was" + userActual.getProfile().getAbout()),
                    () -> assertEquals(userExpected.getProfile().getGender(), userActual.getProfile().getGender(),
                            () -> "should return user with gender: " + userExpected.getProfile().getGender()
                                    + ", but was" + userActual.getProfile().getGender()),
                    () -> assertEquals(userExpected.getProfile().getInterests(), userActual.getProfile().getInterests(),
                            () -> "should return user with interests section: " + userExpected.getProfile().getInterests()
                                    + ", but was" + userActual.getProfile().getInterests()),
                    () -> assertEquals(userExpected.getProfile().getLanguages(), userActual.getProfile().getLanguages(),
                            () -> "should return user with languages section: " + userExpected.getProfile().getLanguages()
                                    + ", but was" + userActual.getProfile().getLanguages()),
                    () -> assertEquals(userExpected.getProfile().getLocation(), userActual.getProfile().getLocation(),
                            () -> "should return user with location: " + userExpected.getProfile().getLocation()
                                    + ", but was" + userActual.getProfile().getLocation()),
                    () -> assertEquals(userExpected.getProfile().getImage(), userActual.getProfile().getImage(),
                            () -> "should return user with image: " + userExpected.getProfile().getImage()
                                    + ", but was" + userActual.getProfile().getImage()),
                    () -> verify(userRepository, times(1))
                            .findByUsername(userExpectedBeforeUpdate.getUsername()),
                    () -> verify(userRepository, times(1)).save(userExpectedAfterObjectMapping),
                    () -> verifyNoMoreInteractions(userRepository),
                    () -> verify(userMapper, times(1))
                            .convertDTOToDocument(userExpectedBeforeUpdate, userUpdateDTOExpected, image),
                    () -> verifyNoMoreInteractions(userMapper),
                    () -> verify(userEventProducer, times(1)).sendUserUpdateEvent(userUpdateEventPayload),
                    () -> verifyNoMoreInteractions(userEventProducer),
                    () -> verifyNoInteractions(roleService));
        }

        @Test
        @SneakyThrows
        void when_update_user_without_profile_should_update_user() {

            UserUpdateDTO userUpdateDTOExpected = (UserUpdateDTO) userTestBuilder.withUsername("validUser")
                    .withEmail("validUser123@email.com").withPassword("ValidPassword123!")
                    .withMatchingPassword("ValidPassword123!").build(ObjectType.UPDATE_DTO);

            MockMultipartFile image = new MockMultipartFile("image", "image", "application/json",
                    "image.jpg".getBytes());

            UserProfileDocument userProfileExpectedBeforeUpdate = (UserProfileDocument) userProfileTestBuilder
                    .withAbout("").withInterests("").withLanguages("").withLocation("").withImage(image.getBytes())
                    .build(ObjectType.DOCUMENT);
            UserDocument userExpectedBeforeUpdate = (UserDocument) userTestBuilder.withUsername("previous username")
                    .withEmail("prevoius@email.com").withPassword("oldPass123!").withProfile(userProfileExpectedBeforeUpdate)
                    .build(ObjectType.DOCUMENT);
            UserProfileDocument userProfileExpectedAfterObjectMapping = (UserProfileDocument) userProfileTestBuilder
                    .withAbout("").withInterests("").withLanguages("").withLocation("").withImage(image.getBytes())
                    .build(ObjectType.DOCUMENT);
            UserDocument userExpectedAfterObjectMapping = (UserDocument) userTestBuilder
                    .withProfile(userProfileExpectedAfterObjectMapping).build(ObjectType.DOCUMENT);
            RoleDocument roleExpected = new RoleDocument(defaultUserRole);
            String passwordEncoded = "encodedPassword";
            UserDocument userExpectedAfterPasswordEncodingAndSettingRoles = (UserDocument) userTestBuilder
                    .withPassword(passwordEncoded).withProfile(userProfileExpectedAfterObjectMapping)
                    .withRoles(Set.of(roleExpected)).build(ObjectType.DOCUMENT);
            UserUpdateEventPayload userUpdateEventPayload =
                    new UserUpdateEventPayload(userExpectedBeforeUpdate.getUsername(), userUpdateDTOExpected.getUsername());
            UserDocument userExpected = (UserDocument) userTestBuilder.withPassword(passwordEncoded)
                    .withProfile(userProfileExpectedAfterObjectMapping).withRoles(Set.of(roleExpected))
                    .build(ObjectType.DOCUMENT);

            when(userRepository.findByUsername(userExpectedBeforeUpdate.getUsername()))
                    .thenReturn(Optional.of(userExpectedBeforeUpdate));
            when(userRepository.save(userExpectedAfterPasswordEncodingAndSettingRoles)).thenReturn(userExpected);

            UserDocument userActual = userService.updateUser(userExpectedBeforeUpdate.getUsername(), userUpdateDTOExpected, image);

            assertAll(() -> assertEquals(userExpected, userActual,
                            () -> "should return user: " + userExpected + ", but was" + userActual),
                    () -> assertEquals(userExpected.getId(), userActual.getId(),
                            () -> "should return user with id: " + userExpected.getId() + ", but was"
                                    + userActual.getId()),
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
                    () -> assertEquals(userExpected.getProfile(), userActual.getProfile(),
                            () -> "should return user with profile: " + userExpected.getProfile()
                                    + ", but was" + userActual.getProfile()),
                    () -> assertEquals(userExpected.getProfile().getAbout(), userActual.getProfile().getAbout(),
                            () -> "should return user with about section: " + userExpected.getProfile().getAbout()
                                    + ", but was" + userActual.getProfile().getAbout()),
                    () -> assertEquals(userExpected.getProfile().getGender(), userActual.getProfile().getGender(),
                            () -> "should return user with gender: " + userExpected.getProfile().getGender()
                                    + ", but was" + userActual.getProfile().getGender()),
                    () -> assertEquals(userExpected.getProfile().getInterests(), userActual.getProfile().getInterests(),
                            () -> "should return user with interests section: " + userExpected.getProfile().getInterests()
                                    + ", but was" + userActual.getProfile().getInterests()),
                    () -> assertEquals(userExpected.getProfile().getLanguages(), userActual.getProfile().getLanguages(),
                            () -> "should return user with languages section: " + userExpected.getProfile().getLanguages()
                                    + ", but was" + userActual.getProfile().getLanguages()),
                    () -> assertEquals(userExpected.getProfile().getLocation(), userActual.getProfile().getLocation(),
                            () -> "should return user with location: " + userExpected.getProfile().getLocation()
                                    + ", but was" + userActual.getProfile().getLocation()),
                    () -> assertEquals(userExpected.getProfile().getImage(), userActual.getProfile().getImage(),
                            () -> "should return user with image: " + userExpected.getProfile().getImage()
                                    + ", but was" + userActual.getProfile().getImage()),
                    () -> verify(userRepository, times(1))
                            .findByUsername(userExpectedBeforeUpdate.getUsername()),
                    () -> verify(userRepository, times(1)).save(userExpectedAfterObjectMapping),
                    () -> verifyNoMoreInteractions(userRepository),
                    () -> verify(userMapper, times(1))
                            .convertDTOToDocument(userExpectedBeforeUpdate, userUpdateDTOExpected, image),
                    () -> verifyNoMoreInteractions(userMapper),
                    () -> verify(userEventProducer, times(1)).sendUserUpdateEvent(userUpdateEventPayload),
                    () -> verifyNoMoreInteractions(userEventProducer),
                    () -> verifyNoInteractions(roleService));
        }

        @Test
        @SneakyThrows
        void when_update_user_without_profile_and_image_should_update_user() {

            UserUpdateDTO userUpdateDTOExpected = (UserUpdateDTO) userTestBuilder.withUsername("validUser")
                    .withEmail("validUser123@email.com").withPassword("ValidPassword123!")
                    .withMatchingPassword("ValidPassword123!").build(ObjectType.UPDATE_DTO);

            UserProfileDocument userProfileExpectedBeforeUpdate = (UserProfileDocument) userProfileTestBuilder
                    .withAbout("").withInterests("").withLanguages("").withLocation("").withImage(null)
                    .build(ObjectType.DOCUMENT);
            UserDocument userExpectedBeforeUpdate = (UserDocument) userTestBuilder.withUsername("previous username")
                    .withEmail("prevoius@email.com").withPassword("oldPass123!").withProfile(userProfileExpectedBeforeUpdate)
                    .build(ObjectType.DOCUMENT);
            UserProfileDocument userProfileExpectedAfterObjectMapping = (UserProfileDocument) userProfileTestBuilder
                    .withAbout("").withInterests("").withLanguages("").withLocation("").withImage(null)
                    .build(ObjectType.DOCUMENT);
            UserDocument userExpectedAfterObjectMapping = (UserDocument) userTestBuilder
                    .withProfile(userProfileExpectedAfterObjectMapping).build(ObjectType.DOCUMENT);
            RoleDocument roleExpected = new RoleDocument(defaultUserRole);
            String passwordEncoded = "encodedPassword";
            UserDocument userExpectedAfterPasswordEncodingAndSettingRoles = (UserDocument) userTestBuilder
                    .withPassword(passwordEncoded).withProfile(userProfileExpectedAfterObjectMapping)
                    .withRoles(Set.of(roleExpected)).build(ObjectType.DOCUMENT);
            UserUpdateEventPayload userUpdateEventPayload =
                    new UserUpdateEventPayload(userExpectedBeforeUpdate.getUsername(), userUpdateDTOExpected.getUsername());
            UserDocument userExpected = (UserDocument) userTestBuilder.withPassword(passwordEncoded)
                    .withProfile(userProfileExpectedAfterObjectMapping).withRoles(Set.of(roleExpected))
                    .build(ObjectType.DOCUMENT);

            when(userRepository.findByUsername(userExpectedBeforeUpdate.getUsername()))
                    .thenReturn(Optional.of(userExpectedBeforeUpdate));
            when(userRepository.save(userExpectedAfterPasswordEncodingAndSettingRoles)).thenReturn(userExpected);

            UserDocument userActual = userService.updateUser(userExpectedBeforeUpdate.getUsername(), userUpdateDTOExpected, null);

            assertAll(() -> assertEquals(userExpected, userActual,
                            () -> "should return user: " + userExpected + ", but was" + userActual),
                    () -> assertEquals(userExpected.getId(), userActual.getId(),
                            () -> "should return user with id: " + userExpected.getId() + ", but was"
                                    + userActual.getId()),
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
                    () -> assertEquals(userExpected.getProfile(), userActual.getProfile(),
                            () -> "should return user with profile: " + userExpected.getProfile()
                                    + ", but was" + userActual.getProfile()),
                    () -> assertEquals(userExpected.getProfile().getAbout(), userActual.getProfile().getAbout(),
                            () -> "should return user with about section: " + userExpected.getProfile().getAbout()
                                    + ", but was" + userActual.getProfile().getAbout()),
                    () -> assertEquals(userExpected.getProfile().getGender(), userActual.getProfile().getGender(),
                            () -> "should return user with gender: " + userExpected.getProfile().getGender()
                                    + ", but was" + userActual.getProfile().getGender()),
                    () -> assertEquals(userExpected.getProfile().getInterests(), userActual.getProfile().getInterests(),
                            () -> "should return user with interests section: " + userExpected.getProfile().getInterests()
                                    + ", but was" + userActual.getProfile().getInterests()),
                    () -> assertEquals(userExpected.getProfile().getLanguages(), userActual.getProfile().getLanguages(),
                            () -> "should return user with languages section: " + userExpected.getProfile().getLanguages()
                                    + ", but was" + userActual.getProfile().getLanguages()),
                    () -> assertEquals(userExpected.getProfile().getLocation(), userActual.getProfile().getLocation(),
                            () -> "should return user with location: " + userExpected.getProfile().getLocation()
                                    + ", but was" + userActual.getProfile().getLocation()),
                    () -> assertEquals(userExpected.getProfile().getImage(), userActual.getProfile().getImage(),
                            () -> "should return user with image: " + userExpected.getProfile().getImage()
                                    + ", but was" + userActual.getProfile().getImage()),
                    () -> verify(userRepository, times(1))
                            .findByUsername(userExpectedBeforeUpdate.getUsername()),
                    () -> verify(userRepository, times(1)).save(userExpectedAfterObjectMapping),
                    () -> verifyNoMoreInteractions(userRepository),
                    () -> verify(userMapper, times(1))
                            .convertDTOToDocument(userExpectedBeforeUpdate, userUpdateDTOExpected, null),
                    () -> verifyNoMoreInteractions(userMapper),
                    () -> verify(userEventProducer, times(1)).sendUserUpdateEvent(userUpdateEventPayload),
                    () -> verifyNoMoreInteractions(userEventProducer),
                    () -> verifyNoInteractions(roleService));
        }

        @Test
        void when_update_not_existing_user_should_throw_exception() {

            UserProfileDTO userProfileDTO = (UserProfileDTO) userProfileTestBuilder.withAbout("new about")
                    .withInterests("new interests").withLanguages("new languages").withLocation("new location")
                    .withGender(Gender.FEMALE).build(ObjectType.UPDATE_DTO);
            UserUpdateDTO userUpdateDTOExpected = (UserUpdateDTO) userTestBuilder.withUsername("validUser")
                    .withEmail("validUser123@email.com").withPassword("ValidPassword123!")
                    .withMatchingPassword("ValidPassword123!").withProfile(userProfileDTO).build(ObjectType.UPDATE_DTO);

            MockMultipartFile image = new MockMultipartFile("image", "image", "application/json",
                    "image.jpg".getBytes());

            String notExistingUsername = "iAmNotExist";

            when(userRepository.findByUsername(notExistingUsername)).thenReturn(Optional.empty());

            assertAll(() -> assertThrows(ResourceNotFoundException.class,
                            () -> userService.updateUser(notExistingUsername, userUpdateDTOExpected, image),
                            "should throw ResourceNotFoundException but wasn't"),
                    () -> verify(userRepository, times(1)).findByUsername(notExistingUsername),
                    () -> verifyNoMoreInteractions(userRepository),
                    () -> verifyNoInteractions(userMapper),
                    () -> verifyNoInteractions(roleService),
                    () -> verifyNoInteractions(userEventProducer));
        }
    }

    @Nested
    class DeleteUserTest {

        @Test
        @SneakyThrows
        void when_delete_existing_user_should_delete_user() {

            MockMultipartFile image = new MockMultipartFile("image", "image", "application/json",
                    "image.jpg".getBytes());
            UserProfileDocument userProfileExpected = (UserProfileDocument) userProfileTestBuilder
                    .withImage(image.getBytes()).build(ObjectType.DOCUMENT);
            RoleDocument roleExpected = new RoleDocument(defaultUserRole);
            UserDocument userExpected = (UserDocument) userTestBuilder.withProfile(userProfileExpected)
                    .withRoles(Set.of(roleExpected)).build(ObjectType.DOCUMENT);

            when(userRepository.findByUsername(userExpected.getUsername())).thenReturn(Optional.of(userExpected));

            assertAll(() -> assertDoesNotThrow(() -> userService.deleteUser(userExpected.getUsername()),
                            "should not throw ResourceNotFoundException or NotAuthorizedException but was"),
                    () -> verify(userRepository, times(1)).findByUsername(userExpected.getUsername()),
                    () -> verify(userRepository, times(1)).delete(userExpected),
                    () -> verifyNoMoreInteractions(userRepository),
                    () -> verify(userEventProducer, times(1)).sendUserDeleteEvent(userExpected.getUsername()),
                    () -> verifyNoMoreInteractions(userEventProducer),
                    () -> verifyNoInteractions(userMapper),
                    () -> verifyNoInteractions(roleService));
        }

        @Test
        void when_delete_not_existing_user_should_throw_exception() {

            String notExistingUsername = "iAmNotExist";

            when(userRepository.findByUsername(notExistingUsername)).thenReturn(Optional.empty());

            assertAll(() -> assertThrows(ResourceNotFoundException.class,
                            () -> userService.deleteUser(notExistingUsername),
                            "should throw ResourceNotFoundException but wasn't"),
                    () -> verify(userRepository, times(1)).findByUsername(notExistingUsername),
                    () -> verifyNoMoreInteractions(userRepository),
                    () -> verifyNoInteractions(userMapper),
                    () -> verifyNoInteractions(roleService),
                    () -> verifyNoInteractions(userEventProducer));
        }
    }

    @Nested
    class FindUserTest {

        @Test
        void when_check_existing_user_if_user_exists_by_username_should_return_true() {

            String expectedUsername = "username";

            when(userRepository.existsUserByUsername(expectedUsername)).thenReturn(true);

            boolean isUsernameInUseActual = userService.isUsernameAlreadyInUse(expectedUsername);

            assertAll(() -> assertTrue(isUsernameInUseActual, () -> "should return true, but was: false"),
                    () -> verify(userRepository, times(1)).existsUserByUsername(expectedUsername),
                    () -> verifyNoMoreInteractions(userRepository),
                    () -> verifyNoInteractions(userMapper),
                    () -> verifyNoInteractions(roleService),
                    () -> verifyNoInteractions(userEventProducer));
        }

        @Test
        void when_check_not_existing_user_if_user_exists_by_username_should_return_false() {

            String expectedUsername = "username";

            when(userRepository.existsUserByUsername(expectedUsername)).thenReturn(false);

            boolean isUsernameInUseActual = userService.isUsernameAlreadyInUse(expectedUsername);

            assertAll(() -> assertFalse(isUsernameInUseActual, () -> "should return false, but was: true"),
                    () -> verify(userRepository, times(1)).existsUserByUsername(expectedUsername),
                    () -> verifyNoMoreInteractions(userRepository),
                    () -> verifyNoInteractions(userMapper),
                    () -> verifyNoInteractions(roleService),
                    () -> verifyNoInteractions(userEventProducer));
        }

        @Test
        void when_check_existing_user_if_user_exists_by_email_should_return_true() {

            String expectedEmail = "user@email.com";

            when(userRepository.existsUserByEmail(expectedEmail)).thenReturn(true);

            boolean isUsernameInUseActual = userService.isEmailAlreadyInUse(expectedEmail);

            assertAll(() -> assertTrue(isUsernameInUseActual, () -> "should return true, but was: false"),
                    () -> verify(userRepository, times(1)).existsUserByEmail(expectedEmail),
                    () -> verifyNoMoreInteractions(userRepository),
                    () -> verifyNoInteractions(userMapper),
                    () -> verifyNoInteractions(roleService),
                    () -> verifyNoInteractions(userEventProducer));
        }

        @Test
        void when_check_not_existing_user_if_user_exists_by_email_should_return_false() {

            String expectedEmail = "user@email.com";

            when(userRepository.existsUserByEmail(expectedEmail)).thenReturn(false);

            boolean isUsernameInUseActual = userService.isEmailAlreadyInUse(expectedEmail);

            assertAll(() -> assertFalse(isUsernameInUseActual, () -> "should return false, but was: true"),
                    () -> verify(userRepository, times(1)).existsUserByEmail(expectedEmail),
                    () -> verifyNoMoreInteractions(userRepository),
                    () -> verifyNoInteractions(userMapper),
                    () -> verifyNoInteractions(roleService),
                    () -> verifyNoInteractions(userEventProducer));
        }

        @Test
        void when_find_existing_user_by_id_should_return_user() {

            Long expectedId = 1L;
            UserProfileDocument userProfileExpected = (UserProfileDocument) userProfileTestBuilder.build(ObjectType.DOCUMENT);
            UserDocument userExpected = (UserDocument) userTestBuilder.withProfile(userProfileExpected).build(ObjectType.DOCUMENT);

            when(userRepository.findById(expectedId)).thenReturn(Optional.of(userExpected));

            Optional<UserDocument> userActualOptional = userService.findById(expectedId);

            assertTrue(userActualOptional.isPresent(), () -> "shouldn't return empty optional");

            UserDocument userActual = userActualOptional.get();

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
                    () -> assertEquals(userExpected.getProfile(), userActual.getProfile(),
                            () -> "should return user with profile: " + userExpected.getProfile()
                                    + ", but was" + userActual.getProfile()),
                    () -> assertEquals(userExpected.getProfile().getAbout(), userActual.getProfile().getAbout(),
                            () -> "should return user with about section: " + userExpected.getProfile().getAbout()
                                    + ", but was" + userActual.getProfile().getAbout()),
                    () -> assertEquals(userExpected.getProfile().getGender(), userActual.getProfile().getGender(),
                            () -> "should return user with gender: " + userExpected.getProfile().getGender()
                                    + ", but was" + userActual.getProfile().getGender()),
                    () -> assertEquals(userExpected.getProfile().getInterests(), userActual.getProfile().getInterests(),
                            () -> "should return user with interests section: " + userExpected.getProfile().getInterests()
                                    + ", but was" + userActual.getProfile().getInterests()),
                    () -> assertEquals(userExpected.getProfile().getLanguages(), userActual.getProfile().getLanguages(),
                            () -> "should return user with languages section: " + userExpected.getProfile().getLanguages()
                                    + ", but was" + userActual.getProfile().getLanguages()),
                    () -> assertEquals(userExpected.getProfile().getLocation(), userActual.getProfile().getLocation(),
                            () -> "should return user with location: " + userExpected.getProfile().getLocation()
                                    + ", but was" + userActual.getProfile().getLocation()),
                    () -> assertEquals(userExpected.getProfile().getImage(), userActual.getProfile().getImage(),
                            () -> "should return user with image: " + userExpected.getProfile().getImage()
                                    + ", but was" + userActual.getProfile().getImage()),
                    () -> verify(userRepository, times(1)).findById(expectedId),
                    () -> verifyNoMoreInteractions(userRepository),
                    () -> verifyNoInteractions(userMapper),
                    () -> verifyNoInteractions(roleService),
                    () -> verifyNoInteractions(userEventProducer));
        }

        @Test
        void when_find_not_existing_user_by_id_should_return_empty_optional() {

            Long notExistingId = 1L;

            when(userRepository.findById(notExistingId)).thenReturn(Optional.empty());

            Optional<UserDocument> userActualOptional = userService.findById(notExistingId);

            assertAll(() -> assertTrue(userActualOptional.isEmpty(),
                            () -> "should return empty optional, but was: " + userActualOptional.get()),
                    () -> verify(userRepository, times(1)).findById(notExistingId),
                    () -> verifyNoMoreInteractions(userRepository),
                    () -> verifyNoInteractions(userMapper),
                    () -> verifyNoInteractions(roleService),
                    () -> verifyNoInteractions(userEventProducer));
        }

        @Test
        void when_find_existing_user_by_username_should_return_user() {

            UserProfileDocument userProfileExpected = (UserProfileDocument) userProfileTestBuilder.build(ObjectType.DOCUMENT);
            UserDocument userExpected = (UserDocument) userTestBuilder.withProfile(userProfileExpected).build(ObjectType.DOCUMENT);

            when(userRepository.findByUsername(userExpected.getUsername())).thenReturn(Optional.of(userExpected));

            Optional<UserDocument> userActualOptional = userService.findByUsername(userExpected.getUsername());

            assertTrue(userActualOptional.isPresent(), () -> "shouldn't return empty optional");

            UserDocument userActual = userActualOptional.get();

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
                    () -> assertEquals(userExpected.getProfile(), userActual.getProfile(),
                            () -> "should return user with profile: " + userExpected.getProfile()
                                    + ", but was" + userActual.getProfile()),
                    () -> assertEquals(userExpected.getProfile().getAbout(), userActual.getProfile().getAbout(),
                            () -> "should return user with about section: " + userExpected.getProfile().getAbout()
                                    + ", but was" + userActual.getProfile().getAbout()),
                    () -> assertEquals(userExpected.getProfile().getGender(), userActual.getProfile().getGender(),
                            () -> "should return user with gender: " + userExpected.getProfile().getGender()
                                    + ", but was" + userActual.getProfile().getGender()),
                    () -> assertEquals(userExpected.getProfile().getInterests(), userActual.getProfile().getInterests(),
                            () -> "should return user with interests section: " + userExpected.getProfile().getInterests()
                                    + ", but was" + userActual.getProfile().getInterests()),
                    () -> assertEquals(userExpected.getProfile().getLanguages(), userActual.getProfile().getLanguages(),
                            () -> "should return user with languages section: " + userExpected.getProfile().getLanguages()
                                    + ", but was" + userActual.getProfile().getLanguages()),
                    () -> assertEquals(userExpected.getProfile().getLocation(), userActual.getProfile().getLocation(),
                            () -> "should return user with location: " + userExpected.getProfile().getLocation()
                                    + ", but was" + userActual.getProfile().getLocation()),
                    () -> assertEquals(userExpected.getProfile().getImage(), userActual.getProfile().getImage(),
                            () -> "should return user with image: " + userExpected.getProfile().getImage()
                                    + ", but was" + userActual.getProfile().getImage()),
                    () -> verify(userRepository, times(1)).findByUsername(userExpected.getUsername()),
                    () -> verifyNoMoreInteractions(userRepository),
                    () -> verifyNoInteractions(userMapper),
                    () -> verifyNoInteractions(roleService),
                    () -> verifyNoInteractions(userEventProducer));
        }

        @Test
        void when_find_existing_user_by_username_should_return_empty_optional() {

            String notExistingUsername = "notExistingUsername";

            when(userRepository.findByUsername(notExistingUsername)).thenReturn(Optional.empty());

            Optional<UserDocument> userActualOptional = userService.findByUsername(notExistingUsername);

            assertAll(() -> assertTrue(userActualOptional.isEmpty(), () -> "should return empty optional, but was: " + userActualOptional.get()),
                    () -> verify(userRepository, times(1)).findByUsername(notExistingUsername),
                    () -> verifyNoMoreInteractions(userRepository),
                    () -> verifyNoInteractions(userMapper),
                    () -> verifyNoInteractions(roleService),
                    () -> verifyNoInteractions(userEventProducer));
        }

        @Test
        void when_find_existing_user_photo_should_return_photo() {

            String expectedUsername = "username";
            byte[] expectedImageBytes = "image.jpg".getBytes();
            UserProfileDocument userProfileExpected = (UserProfileDocument) userProfileTestBuilder
                    .withImage(expectedImageBytes).build(ObjectType.DOCUMENT);
            UserDocument userExpected = (UserDocument) userTestBuilder.withUsername(expectedUsername)
                    .withProfile(userProfileExpected).build(ObjectType.DOCUMENT);
            List<String> expectedUsernames = List.of(expectedUsername);

            when(userRepository.findByUsernameIn(expectedUsernames)).thenReturn(List.of(userExpected));

            List<UserPhotoModel> actualUserPhotos = userService.findUsersPhotosByUsernames(expectedUsernames);

            assertAll(() -> assertEquals(1, actualUserPhotos.size(),
                            () -> "should return one users photo, but was: " + actualUserPhotos),
                    () -> assertEquals(userExpected.getUsername(), actualUserPhotos.get(0).getUsername(),
                            () -> "should return user with username: " + userExpected.getUsername() + ", but was"
                                    + actualUserPhotos.get(0).getUsername()),
                    () -> verify(userRepository, times(1)).findByUsernameIn(expectedUsernames),
                    () -> verifyNoMoreInteractions(userRepository),
                    () -> verifyNoInteractions(userMapper),
                    () -> verifyNoInteractions(roleService),
                    () -> verifyNoInteractions(userEventProducer));
        }

        @Test
        void when_find_existing_and_not_existing_users_photos_should_return_existing_user_photo() {

            String expectedUsername = "username";
            String notExistingUsername = "notExistingUsername";
            byte[] expectedImageBytes = "image.jpg".getBytes();
            List<byte[]> expectedBytes = List.of(expectedImageBytes);
            UserProfileDocument userProfileExpected = (UserProfileDocument) userProfileTestBuilder
                    .withImage(expectedImageBytes).build(ObjectType.DOCUMENT);
            UserDocument userExpected = (UserDocument) userTestBuilder.withUsername(expectedUsername)
                    .withProfile(userProfileExpected).build(ObjectType.DOCUMENT);
            List<String> expectedUsernames = List.of(expectedUsername, notExistingUsername);

            when(userRepository.findByUsernameIn(expectedUsernames)).thenReturn(List.of(userExpected));

            List<UserPhotoModel> actualUserPhotos = userService.findUsersPhotosByUsernames(expectedUsernames);

            assertAll(() -> assertEquals(1, actualUserPhotos.size(),
                            () -> "should return one users photo, but was: " + actualUserPhotos),
                    () -> assertEquals(userExpected.getUsername(), actualUserPhotos.get(0).getUsername(),
                            () -> "should return user with username: " + userExpected.getUsername() + ", but was"
                                    + actualUserPhotos.get(0).getUsername()),
                    () -> verify(userRepository, times(1)).findByUsernameIn(expectedUsernames),
                    () -> verifyNoMoreInteractions(userRepository),
                    () -> verifyNoInteractions(userMapper),
                    () -> verifyNoInteractions(roleService),
                    () -> verifyNoInteractions(userEventProducer));
        }

        @Test
        void when_find_not_existing_user_photo_should_return_empty_array() {

            String expectedUsername = "username";
            List<String> expectedUsernames = List.of(expectedUsername);

            when(userRepository.findByUsernameIn(expectedUsernames)).thenReturn(Collections.emptyList());

            List<UserPhotoModel> actualUserPhotos = userService.findUsersPhotosByUsernames(expectedUsernames);

            assertAll(() -> assertTrue(actualUserPhotos.isEmpty(),
                            () -> "should return empty array, but was: " + actualUserPhotos),
                    () -> verify(userRepository, times(1)).findByUsernameIn(expectedUsernames),
                    () -> verifyNoMoreInteractions(userRepository),
                    () -> verifyNoInteractions(userMapper),
                    () -> verifyNoInteractions(roleService),
                    () -> verifyNoInteractions(userEventProducer));
        }
    }
}
