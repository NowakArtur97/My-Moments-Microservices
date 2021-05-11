package com.nowakArtur97.myMoments.userService.feature.document;

import com.nowakArtur97.myMoments.userService.exception.ResourceNotFoundException;
import com.nowakArtur97.myMoments.userService.feature.messaging.PostEventSender;
import com.nowakArtur97.myMoments.userService.feature.messaging.UserUpdateEventPayload;
import com.nowakArtur97.myMoments.userService.feature.resource.UserRegistrationDTO;
import com.nowakArtur97.myMoments.userService.feature.resource.UserUpdateDTO;
import com.nowakArtur97.myMoments.userService.feature.validation.UserValidationGroupSequence;
import lombok.RequiredArgsConstructor;
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

    private final RoleService roleService;

    private final UserMapper userMapper;

    private final PostEventSender postEventSender;

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

        RoleDocument roleDocument = roleService.findByName(defaultUserRole)
                .orElseThrow(() -> new RoleNotFoundException("Role with name: '" + defaultUserRole + "' not found."));

        UserDocument newUserDocument = userMapper.convertDTOToDocument(userRegistrationDTO, image, roleDocument);

        return userRepository.save(newUserDocument);
    }

    public UserDocument updateUser(String username, @Valid UserUpdateDTO userUpdateDTO, MultipartFile image)
            throws IOException {

        UserDocument userDocument = findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User with username: '" + username + "' not found."));

        boolean shouldSendMessage = !username.equals(userUpdateDTO.getUsername());

        userUpdateDTO.setId(userDocument.getId());

        userMapper.convertDTOToDocument(userDocument, userUpdateDTO, image);

        userDocument = userRepository.save(userDocument);

        if (shouldSendMessage) {

            postEventSender.sendPostCreated(new UserUpdateEventPayload(username, userUpdateDTO.getUsername()));
        }

        return userDocument;
    }

    public void deleteUser(String username) {

        UserDocument userDocument = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User with username: '" + username + "' not found."));

        userRepository.delete(userDocument);
    }
}
