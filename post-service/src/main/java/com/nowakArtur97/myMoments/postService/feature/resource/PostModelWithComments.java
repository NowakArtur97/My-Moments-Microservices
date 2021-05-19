package com.nowakArtur97.myMoments.postService.feature.resource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@ApiModel(description = "Details about the Post")
public class PostModelWithComments extends PostModel {

    @ApiModelProperty(notes = "The post's comments")
    private List<CommentModel> comments;
}
