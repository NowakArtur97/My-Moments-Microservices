package com.nowakArtur97.myMoments.commentService.feature.user;

import com.nowakArtur97.myMoments.commentService.testUtil.enums.ObjectType;

class RoleTestBuilder {

    static RoleDocument DEFAULT_ROLE_ENTITY = new RoleDocument("USER_ROLE");

    private String name = "role";

    RoleTestBuilder withName(String name) {

        this.name = name;

        return this;
    }

    RoleDocument build(ObjectType type) {

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
