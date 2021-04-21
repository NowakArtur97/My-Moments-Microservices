package com.nowakArtur97.myMoments.userService.document;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user")
class UserDocument extends AbstractDocument{

    private String username;

    private String email;

    private String password;
}
