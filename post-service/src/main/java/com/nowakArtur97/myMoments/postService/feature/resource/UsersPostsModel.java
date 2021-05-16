package com.nowakArtur97.myMoments.postService.feature.resource;

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
@ApiModel(description = "Details about the User's Posts")
class UsersPostsModel {

    @ApiModelProperty(notes = "The user's posts")
    private final List<PostModel> posts;

    public UsersPostsModel() {
        this.posts = new ArrayList<>();
    }
}
