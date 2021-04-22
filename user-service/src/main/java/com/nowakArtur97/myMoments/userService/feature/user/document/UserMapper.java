package com.nowakArtur97.myMoments.feature.user.entity;

import com.nowakArtur97.myMoments.feature.user.resource.UserDTO;
import com.nowakArtur97.myMoments.feature.user.resource.UserProfileDTO;
import com.nowakArtur97.myMoments.feature.user.resource.UserRegistrationDTO;
import com.nowakArtur97.myMoments.feature.user.resource.UserUpdateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder bCryptPasswordEncoder;

    public UserEntity convertDTOToEntity(UserRegistrationDTO userRegistrationDTO, MultipartFile image, RoleEntity role)
            throws IOException {

        UserEntity userEntity = new UserEntity();
        UserProfileEntity userProfileEntity = new UserProfileEntity();
        userEntity.setProfile(userProfileEntity);

        setUp(userEntity, userRegistrationDTO, image);

        userEntity.addRole(role);

        return userEntity;
    }

    public void convertDTOToEntity(UserEntity userEntity, UserUpdateDTO userUpdateDTO, MultipartFile image) throws IOException {
        setUp(userEntity, userUpdateDTO, image);
    }

    private void setUp(UserEntity userEntity, UserDTO userRegistrationDTO, MultipartFile image) throws IOException {

        setUserProperties(userRegistrationDTO, userEntity, image);

        UserProfileDTO userProfileDTO = userRegistrationDTO.getProfile();
        UserProfileEntity userProfileEntity = userEntity.getProfile();

        if (userProfileDTO != null) {

            setupUserProfileProperties(userProfileEntity, userProfileDTO);
        }

        setupDefaultProfileValues(userEntity, userProfileEntity);
    }

    private void setUserProperties(UserDTO userRegistrationDTO, UserEntity userEntity, MultipartFile image) throws IOException {

        userEntity.setUsername(userRegistrationDTO.getUsername());
        userEntity.setEmail(userRegistrationDTO.getEmail());
        userEntity.setPassword(bCryptPasswordEncoder.encode(userRegistrationDTO.getPassword()));
        userEntity.getProfile().setImage(image != null ? image.getBytes() : null);
    }

    private void setupUserProfileProperties(UserProfileEntity userProfileEntity, UserProfileDTO userProfileDTO) {

        userProfileEntity.setAbout(userProfileDTO.getAbout());

        String userGender = userProfileDTO.getGender();
        if (userGender != null) {
            userProfileEntity.setGender(Arrays.stream(Gender.values())
                    .filter(gender -> gender.name().equals(userGender.toUpperCase()))
                    .findFirst()
                    .orElse(Gender.UNSPECIFIED));
        }
        userProfileEntity.setInterests(userProfileDTO.getInterests());
        userProfileEntity.setLanguages(userProfileDTO.getLanguages());
        userProfileEntity.setLocation(userProfileDTO.getLocation());
    }

    private void setupDefaultProfileValues(UserEntity userEntity, UserProfileEntity userProfileEntity) {

        userProfileEntity.setId(userEntity.getId());
        userProfileEntity.setUser(userEntity);

        if (userProfileEntity.getGender() == null) {
            userProfileEntity.setGender(Gender.UNSPECIFIED);
        }
        if (userProfileEntity.getAbout() == null) {
            userProfileEntity.setAbout("");
        }
        if (userProfileEntity.getInterests() == null) {
            userProfileEntity.setInterests("");
        }
        if (userProfileEntity.getLanguages() == null) {
            userProfileEntity.setLanguages("");
        }
        if (userProfileEntity.getLocation() == null) {
            userProfileEntity.setLocation("");
        }
    }
}
