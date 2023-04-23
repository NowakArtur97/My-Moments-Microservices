package com.nowakArtur97.myMoments.userService.feature.resource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@ApiModel(description = "Details about the Users photos")
public class UsersPhotosModel {

    @ApiModelProperty(notes = "The users photos")
    private List<byte[]> photos;
}
