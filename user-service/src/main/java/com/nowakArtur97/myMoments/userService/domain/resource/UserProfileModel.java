package com.nowakArtur97.myMoments.userService.domain.resource;

import com.nowakArtur97.myMoments.userService.domain.common.UserProfile;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@ApiModel(description = "Details about the User's Profile")
public class UserProfileModel implements UserProfile {

    @ApiModelProperty(notes = "The user's information")
    private String about;

    @ApiModelProperty(notes = "The user's gender")
    private String gender;

    @ApiModelProperty(notes = "The user's interests")
    private String interests;

    @ApiModelProperty(notes = "The user's languages")
    private String languages;

    @ApiModelProperty(notes = "The user's location")
    private String location;
}
