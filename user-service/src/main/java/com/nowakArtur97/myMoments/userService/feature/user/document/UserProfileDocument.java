package com.nowakArtur97.myMoments.userService.feature.user.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.bson.types.Binary;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "profile")
@AllArgsConstructor
@Getter
@ToString
class UserProfileDocument extends AbstractDocument {

    private String about;

    private Gender gender;

    private String interests;

    private String languages;

    private String location;

    @ToString.Exclude
    private Binary image;
}
