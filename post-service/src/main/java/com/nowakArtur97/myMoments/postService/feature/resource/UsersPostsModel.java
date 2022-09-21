package com.nowakArtur97.myMoments.postService.feature.resource;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

@Getter
@Setter
@AllArgsConstructor
@ToString
@Schema(description = "Details about the User's Posts")
class UsersPostsModel {

    @Schema(accessMode = READ_ONLY, description = "The user's posts")
    private final List<PostModel> posts;

    public UsersPostsModel() {
        this.posts = new ArrayList<>();
    }
}
