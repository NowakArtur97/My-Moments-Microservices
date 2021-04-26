package com.nowakArtur97.myMoments.userService.eventListener;

import com.nowakArtur97.myMoments.userService.feature.document.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
class OnApplicationStartupEventListener {

    @Value("${my-moments.default-user-role:USER_ROLE}")
    private String defaultUserRole;

    @Value("${my-moments.admin-role:ADMIN_ROLE}")
    private String adminRole;

    @Autowired
    private final RoleRepository roleRepository;

    @Autowired
    private final UserRepository userRepository;

    @EventListener
    void onApplicationStartup(ContextRefreshedEvent event) {

        saveDefaultUser("user", "I am a user", Gender.MALE, "I like cats",
                "Polish and English", "Poland", "user@email.com",
                "$2y$10$55xnu/C0hSlu870m8.n3D.MlNdjYE4Y0X8DIhj7.m89zwSJQllFqy",
                defaultUserRole);
        saveDefaultUser("admin", "I am a admin", Gender.FEMALE, "I like alpacas",
                "Polish and Japanese", "Japan", "admin@email.com",
                "$2y$10$vU366Dmp7ZsASly4kHF0NuCbibOnLEom9W.ocPPTSIfloWUbvnM/e",
                adminRole);

    }

    private void saveDefaultUser(String username, String about, Gender gender, String interests, String languages,
                                 String location, String email, String password, String role) {

        Optional<RoleDocument> userRoleDocumentOptional = roleRepository.findByName(role);

        RoleDocument userRoleDocument = userRoleDocumentOptional.orElseGet(() -> new RoleDocument(role));

        if (userRoleDocumentOptional.isEmpty()) {
            roleRepository.save(userRoleDocument);
        }

        UserProfileDocument userProfileDocument = new UserProfileDocument(about, gender, interests,
                languages, location, null);
        UserDocument userDocument = new UserDocument(username, email, password, userProfileDocument, Set.of(userRoleDocument));

        if (!userRepository.existsUserByUsername(username)) {
            userRepository.save(userDocument);
        }
    }
}
