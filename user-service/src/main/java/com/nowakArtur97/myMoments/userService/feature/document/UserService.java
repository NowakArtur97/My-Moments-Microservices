package com.nowakArtur97.myMoments.userService.feature.document;

import com.nowakArtur97.myMoments.userService.exception.ResourceNotFoundException;
import com.nowakArtur97.myMoments.userService.feature.messaging.UserEventProducer;
import com.nowakArtur97.myMoments.userService.feature.messaging.UserUpdateEventPayload;
import com.nowakArtur97.myMoments.userService.feature.resource.UserRegistrationDTO;
import com.nowakArtur97.myMoments.userService.feature.resource.UserUpdateDTO;
import com.nowakArtur97.myMoments.userService.feature.validation.UserValidationGroupSequence;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import javax.management.relation.RoleNotFoundException;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Validated({UserValidationGroupSequence.class})
@Slf4j
public class UserService {

    @Value("${my-moments.default-user-role:USER_ROLE}")
    private String defaultUserRole;

    private final UserRepository userRepository;

    private final RoleService roleService;

    private final UserMapper userMapper;

    private final UserEventProducer userEventProducer;

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

    public List<byte[]> findUserPhotosByUsernames(List<String> usernames) {

        log.info("Looking up Photos of users: {}", usernames);

        return userRepository.findByUsernameIn(usernames).stream()
                .map(userNode -> userNode.getProfile().getImage())
                .map(Binary::getData)
                .collect(Collectors.toList());
    }

    public UserDocument registerUser(@Valid UserRegistrationDTO userRegistrationDTO, MultipartFile image)
            throws RoleNotFoundException, IOException {

        log.info("Creating a new User: {}", userRegistrationDTO.getUsername());

        RoleDocument roleDocument = roleService.findByName(defaultUserRole)
                .orElseThrow(() -> new RoleNotFoundException("Role with name: '" + defaultUserRole + "' not found."));

        UserDocument newUserDocument = userMapper.convertDTOToDocument(userRegistrationDTO, image, roleDocument);

        newUserDocument = userRepository.save(newUserDocument);

        log.info("Successfully created a User: {}", newUserDocument.getUsername());

        return newUserDocument;
    }

    public UserDocument updateUser(String username, @Valid UserUpdateDTO userUpdateDTO, MultipartFile image)
            throws IOException {

        UserDocument userDocument = findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User with username: '" + username + "' not found."));

        log.info("Updating a User: {}", userDocument.getUsername());

        userUpdateDTO.setId(userDocument.getId());

        userMapper.convertDTOToDocument(userDocument, userUpdateDTO, image);

        userDocument = userRepository.save(userDocument);

        userEventProducer.sendUserUpdateEvent(new UserUpdateEventPayload(username, userUpdateDTO.getUsername()));

        log.info("Successfully updated a User: {}", userDocument.getUsername());

        return userDocument;
    }

    public void deleteUser(String username) {

        log.info("Deleting a User: {}", username);

        UserDocument userDocument = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User with username: '" + username + "' not found."));

        userRepository.delete(userDocument);

        userEventProducer.sendUserDeleteEvent(username);

        log.info("Successfully deleted a User: {}", username);
    }
}
