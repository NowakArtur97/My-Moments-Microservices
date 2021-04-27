package com.nowakArtur97.myMoments.postService.feature.user.document;

import com.nowakArtur97.myMoments.postService.common.document.AbstractDocument;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

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

    public UserDocument(String username, String email, String password) {

        this.username = username;
        this.email = email;
        this.password = password;
    }
}
