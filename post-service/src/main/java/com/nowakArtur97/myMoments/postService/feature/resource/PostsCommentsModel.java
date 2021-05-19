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
@ApiModel(description = "Details about the Post's Comments")
public class PostsCommentsModel {

    @ApiModelProperty(notes = "The post's comments")
    private final List<CommentModel> comments;

    PostsCommentsModel() {
        this.comments = new ArrayList<>();
    }
}
