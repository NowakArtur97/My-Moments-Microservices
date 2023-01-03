package com.nowakArtur97.myMoments.commentService.feature.resource;

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
@Schema(description = "Details about the Post's Comments")
class PostsCommentsModel {

    @Schema(accessMode = READ_ONLY, description = "The post's comments")
    private final List<CommentModel> comments;

    PostsCommentsModel() {
        this.comments = new ArrayList<>();
    }
}
