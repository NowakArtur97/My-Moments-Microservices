package com.nowakArtur97.myMoments.postService.feature.resource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
@ApiModel(description = "Details about the Post")
public class PostModelWithComments extends PostModel {

    @ApiModelProperty(notes = "The post's comments")
    private List<CommentModel> comments;

    public PostModelWithComments(String id, String caption, String author, List<CommentModel> comments) {
        super(id, caption, author);
        this.comments = comments;
    }
}
