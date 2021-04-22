package com.nowakArtur97.myMoments.userService.feature.user.document;

import com.nowakArtur97.myMoments.userService.feature.user.resource.UserRegistrationDTO;
import com.nowakArtur97.myMoments.userService.feature.user.validation.UserValidationGroupSequence;
import lombok.RequiredArgsConstructor;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import javax.management.relation.RoleNotFoundException;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Validated({UserValidationGroupSequence.class})
public class UserService {

    @Value("${my-moments.default-user-role:USER_ROLE}")
    private String defaultUserRole;

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    private final RoleService roleService;

    public boolean isUsernameAlreadyInUse(String username) {

        return userRepository.existsUserByUsername(username);
    }

    public boolean isEmailAlreadyInUse(String email) {

        return userRepository.existsUserByEmail(email);
    }

    public Optional<UserDocument> findById(Long id) {

        return userRepository.findById(id);
    }

    public Optional<UserDocument> findByUsername(String username) {

        return userRepository.findByUsername(username);
    }

    public UserDocument registerUser(@Valid UserRegistrationDTO userRegistrationDTO, MultipartFile image)
            throws RoleNotFoundException, IOException {

        RoleDocument roleDocument = roleService.findByName(defaultUserRole).orElse(new RoleDocument(defaultUserRole));

        UserDocument newUserDocument = modelMapper.map(userRegistrationDTO, UserDocument.class);
        newUserDocument.addRole(roleDocument);
        newUserDocument.getProfile().setImage(new Binary(BsonBinarySubType.BINARY, image.getBytes()));

        return userRepository.save(newUserDocument);
    }
}
