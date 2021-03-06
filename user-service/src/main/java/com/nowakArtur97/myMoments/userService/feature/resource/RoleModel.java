package com.nowakArtur97.myMoments.userService.feature.resource;

import com.nowakArtur97.myMoments.userService.feature.common.Role;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@ApiModel(description = "Details about the User's Role")
public class RoleModel implements Role {

    @ApiModelProperty(notes = "The role's name")
    private String name;
}
