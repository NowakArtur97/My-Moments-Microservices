package com.nowakArtur97.myMoments.postService.feature.user.document;

import com.nowakArtur97.myMoments.postService.common.document.AbstractDocument;
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
public class UserDocument extends AbstractDocument {

    @Indexed(unique = true)
    private String username;

    @Indexed(unique = true)
    private String email;

    private String password;

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

    public UserDocument(String username, String email, String password, Set<RoleDocument> roles) {

        this.username = username;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }
}
