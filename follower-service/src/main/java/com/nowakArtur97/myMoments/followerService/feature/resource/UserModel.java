package com.nowakArtur97.myMoments.followerService.feature.resource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@ApiModel(description = "Details about the User")
public class UserModel implements User {

    @ApiModelProperty(notes = "The user's name")
    private String username;
}
