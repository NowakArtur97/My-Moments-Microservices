package com.nowakArtur97.myMoments.postService.feature.user.testBuilder;

import com.nowakArtur97.myMoments.postService.feature.user.document.RoleDocument;
import com.nowakArtur97.myMoments.postService.feature.user.document.UserDocument;

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

    public UserTestBuilder withPassword(String password) {

        this.password = password;

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

    public UserDocument build() {

        resetProperties();

        return new UserDocument(username, email, password, roles);
    }

    private void resetProperties() {

        username = "user123";

        password = "SecretPassword123!@";

        email = "userEmail123@email.com";

        roles = new HashSet<>();
    }
}
