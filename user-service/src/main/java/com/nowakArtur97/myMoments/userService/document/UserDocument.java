package com.nowakArtur97.myMoments.userService.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Document(collection = "user")
@AllArgsConstructor
@Getter
@ToString
class UserDocument extends AbstractDocument {

    @Indexed(unique = true)
    private String username;

    @Indexed(unique = true)
    private String email;

    private String password;

    private final Set<RoleDocument> roles;

    public void addRole(RoleDocument role) {

        this.getRoles().add(role);
    }

    public void removeRole(RoleDocument role) {

        this.getRoles().remove(role);
    }
}
