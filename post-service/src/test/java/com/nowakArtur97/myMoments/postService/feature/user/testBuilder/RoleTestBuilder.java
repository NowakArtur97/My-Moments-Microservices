package com.nowakArtur97.myMoments.postService.feature.user.testBuilder;

import com.nowakArtur97.myMoments.postService.enums.ObjectType;
import com.nowakArtur97.myMoments.postService.feature.user.document.RoleDocument;

public class RoleTestBuilder {

    public static RoleDocument DEFAULT_ROLE_ENTITY = new RoleDocument("USER_ROLE");

    private String name = "role";

    public RoleTestBuilder withName(String name) {

        this.name = name;

        return this;
    }

    public RoleDocument build(ObjectType type) {

        RoleDocument role;

        switch (type) {

            case DOCUMENT:

                role = new RoleDocument(name);

                break;

            default:
                throw new RuntimeException("The specified type does not exist");
        }

        resetProperties();

        return role;
    }

    private void resetProperties() {

        name = "role";
    }
}
