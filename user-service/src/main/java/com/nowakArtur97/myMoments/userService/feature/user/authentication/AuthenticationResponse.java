package com.nowakArtur97.myMoments.userService.feature.user.authentication;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@ApiModel(description = "API key")
public class AuthenticationResponse {

    @ApiModelProperty(notes = "Generated token")
    private final String token;

    @ApiModelProperty(notes = "Expiration time in milliseconds")
    private final long expirationTimeInMilliseconds;
}
