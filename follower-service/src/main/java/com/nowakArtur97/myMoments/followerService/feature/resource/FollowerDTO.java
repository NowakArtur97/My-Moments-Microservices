package com.nowakArtur97.myMoments.followerService.feature.resource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Model responsible for Follower's validation")
public class FollowerDTO {

    @NotBlankParam(message = "{follower.username.blank}")
    @ApiModelProperty(notes = "The follower's username")
    private String Username;
}
