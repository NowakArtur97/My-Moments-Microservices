package com.nowakArtur97.myMoments.userService.feature.testBuilder;


import com.nowakArtur97.myMoments.userService.feature.common.UserProfile;
import com.nowakArtur97.myMoments.userService.feature.document.Gender;
import com.nowakArtur97.myMoments.userService.feature.document.UserProfileDocument;
import com.nowakArtur97.myMoments.userService.feature.resource.UserProfileDTO;
import com.nowakArtur97.myMoments.userService.feature.resource.UserProfileModel;
import com.nowakArtur97.myMoments.userService.testUtil.enums.ObjectType;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;

public class UserProfileTestBuilder {

    private String about = "about";

    private Gender gender = Gender.UNSPECIFIED;

    private String genderString = "UNSPECIFIED";

    private String interests = "interests";

    private String languages = "languages";

    private String location = "location";

    private byte[] image = new byte[0];

    public UserProfileTestBuilder withAbout(String about) {

        this.about = about;

        return this;
    }

    public UserProfileTestBuilder withGender(Gender gender) {

        this.gender = gender;

        return this;
    }

    public UserProfileTestBuilder withGender(String gender) {

        this.genderString = gender;

        return this;
    }

    public UserProfileTestBuilder withInterests(String interests) {

        this.interests = interests;

        return this;
    }

    public UserProfileTestBuilder withLanguages(String languages) {

        this.languages = languages;

        return this;
    }

    public UserProfileTestBuilder withLocation(String location) {

        this.location = location;

        return this;
    }

    public UserProfileTestBuilder withImage(byte[] image) {

        this.image = image;

        return this;
    }

    public UserProfile build(ObjectType type) {

        UserProfile userProfile;

        switch (type) {

            case CREATE_DTO:
            case UPDATE_DTO:

                userProfile = new UserProfileDTO(about, genderString, interests, languages, location);

                break;

            case DOCUMENT:

                userProfile = new UserProfileDocument(about, gender, interests, languages, location,
                        image != null ?  new Binary(BsonBinarySubType.BINARY, image) : null);

                break;

            case MODEL:

                userProfile = new UserProfileModel(about, gender.name(), interests, languages, location,
                        image != null ?  image : null);

                break;

            default:
                throw new RuntimeException("The specified type does not exist");
        }

        resetProperties();

        return userProfile;
    }

    private void resetProperties() {

        about = "about";

        gender = Gender.UNSPECIFIED;

        genderString = "UNSPECIFIED";

        interests = "interests";

        languages = "languages";

        location = "location";

        image = new byte[0];
    }
}
