package com.nowakArtur97.myMoments.userService.feature.user.authentication;

import com.nowakArtur97.myMoments.userService.feature.user.common.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "User data required for Authentication")
public class AuthenticationRequest implements User {

    @ApiModelProperty(notes = "The user's name")
    private String username;

    @ApiModelProperty(notes = "The user's password", required = true)
    private String password;

    @ApiModelProperty(notes = "The user's email")
    private String email;
}
