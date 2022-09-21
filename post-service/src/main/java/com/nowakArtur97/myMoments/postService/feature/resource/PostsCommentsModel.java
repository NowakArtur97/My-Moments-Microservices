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
@Schema(description = "Details about the Post's Comments")
public class PostsCommentsModel {

    @Schema(accessMode = READ_ONLY, description = "The post's comments")
    private final List<CommentModel> comments;

    public PostsCommentsModel() {
        this.comments = new ArrayList<>();
    }
}
