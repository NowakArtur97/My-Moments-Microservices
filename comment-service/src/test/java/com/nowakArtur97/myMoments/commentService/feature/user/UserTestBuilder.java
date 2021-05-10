package com.nowakArtur97.myMoments.commentService.feature.user;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class UserTestBuilder {

    private String username = "user123";

    private String password = "SecretPassword123!@";

    private String email = "userEmail123@email.com";

    private Set<RoleDocument> roles = new HashSet<>(Collections.singletonList(RoleTestBuilder.DEFAULT_ROLE_ENTITY));

    public UserTestBuilder withUsername(String username) {

        this.username = username;

        return this;
    }

    UserTestBuilder withPassword(String password) {

        this.password = password;

        return this;
    }

    UserTestBuilder withEmail(String email) {

        this.email = email;

        return this;
    }

    UserTestBuilder withRoles(Set<RoleDocument> roles) {

        this.roles = roles;

        return this;
    }

    public UserDocument build() {

        UserDocument userDocument = new UserDocument(username, email, password, roles);

        resetProperties();

        return userDocument;
    }

    private void resetProperties() {

        username = "user123";

        password = "SecretPassword123!@";

        email = "userEmail123@email.com";

        roles = new HashSet<>();
    }
}
