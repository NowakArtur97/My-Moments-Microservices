package com.nowakArtur97.myMoments.userService.feature.user.document;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.Binary;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "profile")
@Getter
@Setter
@ToString
class UserProfileDocument extends AbstractDocument {

    private String about;

    private Gender gender;

    private String interests;

    private String languages;

    private String location;

    @ToString.Exclude
    private Binary image;

    public UserProfileDocument() {
        super();
    }

    public UserProfileDocument(String about, Gender gender, String interests, String languages, String location, Binary image) {

        this();
        this.about = about;
        this.gender = gender;
        this.interests = interests;
        this.languages = languages;
        this.location = location;
        this.image = image;
    }
}
