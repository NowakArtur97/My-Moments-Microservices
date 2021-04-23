package com.nowakArtur97.myMoments.userService.feature.user.testBuilder;

import com.nowakArtur97.myMoments.userService.feature.user.authentication.AuthenticationRequest;
import com.nowakArtur97.myMoments.userService.feature.user.common.User;
import com.nowakArtur97.myMoments.userService.feature.user.common.UserProfile;
import com.nowakArtur97.myMoments.userService.feature.user.document.RoleDocument;
import com.nowakArtur97.myMoments.userService.feature.user.document.UserDocument;
import com.nowakArtur97.myMoments.userService.feature.user.document.UserProfileDocument;
import com.nowakArtur97.myMoments.userService.feature.user.resource.UserProfileDTO;
import com.nowakArtur97.myMoments.userService.feature.user.resource.UserRegistrationDTO;
import com.nowakArtur97.myMoments.userService.feature.user.resource.UserUpdateDTO;
import com.nowakArtur97.myMoments.userService.testUtil.enums.ObjectType;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class UserTestBuilder {

    private String username = "user123";

    private String password = "SecretPassword123!@";

    private String matchingPassword = "SecretPassword123!@";

    private String email = "userEmail123@email.com";

    private UserProfile profile;

    private Set<RoleDocument> roles = new HashSet<>(Collections.singletonList(RoleTestBuilder.DEFAULT_ROLE_ENTITY));

    public UserTestBuilder withUsername(String username) {

        this.username = username;

        return this;
    }

    public UserTestBuilder withPassword(String password) {

        this.password = password;

        return this;
    }

    public UserTestBuilder withMatchingPassword(String matchingPassword) {

        this.matchingPassword = matchingPassword;

        return this;
    }

    public UserTestBuilder withEmail(String email) {

        this.email = email;

        return this;
    }

    public UserTestBuilder withRoles(Set<RoleDocument> roles) {

        this.roles = roles;

        return this;
    }

    public UserTestBuilder withProfile(UserProfile profile) {

        this.profile = profile;

        return this;
    }

    public User build(ObjectType type) {

        User user = null;

        switch (type) {

            case CREATE_DTO:

                user = new UserRegistrationDTO(username, email, password, matchingPassword, (UserProfileDTO) profile);

                break;

            case UPDATE_DTO:

                user = new UserUpdateDTO(username, email, password, matchingPassword, (UserProfileDTO) profile);

                break;

            case DOCUMENT:

                user = new UserDocument(username, email, password, (UserProfileDocument) profile, roles);

                break;

            case REQUEST:

                user = new AuthenticationRequest(username, password, email);

                break;

            default:
                throw new RuntimeException("The specified type does not exist");
        }

        resetProperties();

        return user;
    }

    private void resetProperties() {

        username = "user123";

        password = "SecretPassword123!@";

        matchingPassword = "SecretPassword123!@";

        email = "userEmail123@email.com";

        profile = null;

        roles = new HashSet<>();
    }
}
