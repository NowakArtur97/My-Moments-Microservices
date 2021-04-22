package com.nowakArtur97.myMoments.userService.feature.user.document;

import com.nowakArtur97.myMoments.userService.feature.user.common.UserProfile;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.Binary;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "profile")
@NoArgsConstructor
@Getter
@Setter
@ToString
class UserProfileDocument extends AbstractDocument implements UserProfile {

    private String about;

    private Gender gender;

    private String interests;

    private String languages;

    private String location;

    @ToString.Exclude
    private Binary image;

    public UserProfileDocument(String about, Gender gender, String interests, String languages, String location, Binary image) {

        this.about = about;
        this.gender = gender;
        this.interests = interests;
        this.languages = languages;
        this.location = location;
        this.image = image;
    }
}
