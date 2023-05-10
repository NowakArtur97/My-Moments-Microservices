package com.nowakArtur97.myMoments.userService.feature.resource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@ApiModel(description = "Details about the User's photo")
public class UserPhotoModel {

    @ApiModelProperty(notes = "The user's name")
    private String username;

    @ApiModelProperty(notes = "The user's image")
    private byte[] image;
}
