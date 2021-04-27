package com.nowakArtur97.myMoments.userService.feature.document;

import com.nowakArtur97.myMoments.userService.feature.common.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Document(collection = "user")
@Getter
@Setter
@ToString
public class UserDocument extends AbstractDocument implements User {

    @Indexed(unique = true)
    private String username;

    @Indexed(unique = true)
    private String email;

    private String password;

    private UserProfileDocument profile;

    private Set<RoleDocument> roles;

    public void addRole(RoleDocument role) {

        this.getRoles().add(role);
    }

    public void removeRole(RoleDocument role) {

        this.getRoles().remove(role);
    }

    public UserDocument() {
        this.roles = new HashSet<>();
    }

    public UserDocument(String username, String email, String password, UserProfileDocument profile, Set<RoleDocument> roles) {

        this.username = username;
        this.email = email;
        this.password = password;
        this.profile = profile;
        this.roles = roles;
    }
}
