package com.nowakArtur97.myMoments.followerService.feature.resource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@ToString
@ApiModel(description = "Details about the User's Acquaintances")
public class UsersAcquaintancesModel {

    @ApiModelProperty(notes = "The user's acquaintances")
    private final List<UserModel> users;

    public UsersAcquaintancesModel() {
        this.users = new ArrayList<>();
    }
}
