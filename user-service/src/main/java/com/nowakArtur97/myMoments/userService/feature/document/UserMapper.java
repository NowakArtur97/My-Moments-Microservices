package com.nowakArtur97.myMoments.userService.feature.document;

import com.nowakArtur97.myMoments.userService.feature.resource.UserProfileDTO;
import com.nowakArtur97.myMoments.userService.feature.resource.UserDTO;
import com.nowakArtur97.myMoments.userService.feature.resource.UserRegistrationDTO;
import com.nowakArtur97.myMoments.userService.feature.resource.UserUpdateDTO;
import lombok.RequiredArgsConstructor;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder bCryptPasswordEncoder;

    public UserDocument convertDTOToDocument(UserRegistrationDTO userRegistrationDTO, MultipartFile image, RoleDocument role)
            throws IOException {

        UserDocument userDocument = new UserDocument();
        UserProfileDocument userProfileDocument = new UserProfileDocument();
        userDocument.setProfile(userProfileDocument);

        setUp(userDocument, userRegistrationDTO, image);

        userDocument.addRole(role);

        return userDocument;
    }

    public void convertDTOToDocument(UserDocument userDocument, UserUpdateDTO userUpdateDTO, MultipartFile image)
            throws IOException {
        setUp(userDocument, userUpdateDTO, image);
    }

    private void setUp(UserDocument userDocument, UserDTO userRegistrationDTO, MultipartFile image) throws IOException {

        setUserProperties(userRegistrationDTO, userDocument, image);

        UserProfileDTO userProfileDTO = userRegistrationDTO.getProfile();
        UserProfileDocument userProfileDocument = userDocument.getProfile();

        if (userProfileDTO != null) {

            setupUserProfileProperties(userProfileDocument, userProfileDTO);
        }

        setupDefaultProfileValues(userDocument, userProfileDocument);
    }

    private void setUserProperties(UserDTO userRegistrationDTO, UserDocument userDocument, MultipartFile image) throws IOException {

        userDocument.setUsername(userRegistrationDTO.getUsername());
        userDocument.setEmail(userRegistrationDTO.getEmail());
        userDocument.setPassword(bCryptPasswordEncoder.encode(userRegistrationDTO.getPassword()));
        userDocument.getProfile().setImage(image != null ? new Binary(BsonBinarySubType.BINARY, image.getBytes()) : null);
    }

    private void setupUserProfileProperties(UserProfileDocument userProfileDocument, UserProfileDTO userProfileDTO) {

        userProfileDocument.setAbout(userProfileDTO.getAbout());

        String userGender = userProfileDTO.getGender();
        if (userGender != null) {
            userProfileDocument.setGender(Arrays.stream(Gender.values())
                    .filter(gender -> gender.name().equals(userGender.toUpperCase()))
                    .findFirst()
                    .orElse(Gender.UNSPECIFIED));
        }
        userProfileDocument.setInterests(userProfileDTO.getInterests());
        userProfileDocument.setLanguages(userProfileDTO.getLanguages());
        userProfileDocument.setLocation(userProfileDTO.getLocation());
    }

    private void setupDefaultProfileValues(UserDocument userDocument, UserProfileDocument userProfileDocument) {

        userProfileDocument.setId(userDocument.getId());

        if (userProfileDocument.getGender() == null) {
            userProfileDocument.setGender(Gender.UNSPECIFIED);
        }
        if (userProfileDocument.getAbout() == null) {
            userProfileDocument.setAbout("");
        }
        if (userProfileDocument.getInterests() == null) {
            userProfileDocument.setInterests("");
        }
        if (userProfileDocument.getLanguages() == null) {
            userProfileDocument.setLanguages("");
        }
        if (userProfileDocument.getLocation() == null) {
            userProfileDocument.setLocation("");
        }
    }
}
