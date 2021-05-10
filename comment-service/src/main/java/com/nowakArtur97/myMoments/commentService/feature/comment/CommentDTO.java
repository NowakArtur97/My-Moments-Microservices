package com.nowakArtur97.myMoments.commentService.feature.comment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@ApiModel(description = "Model responsible for Comment's validation")
class CommentDTO implements Comment {

    @NotBlank(message = "{comment.content.notBlank}")
    @Size(max = 200, message = "{comment.content.size}")
    @ApiModelProperty(notes = "The comment's content")
    private String content;
}

