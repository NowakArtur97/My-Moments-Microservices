package com.nowakArtur97.myMoments.feature.user.testBuilder;

import com.nowakArtur97.myMoments.feature.user.entity.RoleEntity;
import com.nowakArtur97.myMoments.testUtil.enums.ObjectType;

public class RoleTestBuilder {

    public static RoleEntity DEFAULT_ROLE_ENTITY = new RoleEntity("USER_ROLE");

    private String name = "role";

    public RoleTestBuilder withName(String name) {

        this.name = name;

        return this;
    }

    public RoleEntity build(ObjectType type) {

        RoleEntity role;

        switch (type) {

            case ENTITY:

                role = new RoleEntity(name);

                break;

            default:
                throw new RuntimeException("The specified type does not exist");
        }

        resetProperties();

        return role;
    }

    private void resetProperties(){

        name = "role";
    }
}
