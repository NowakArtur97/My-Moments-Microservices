package com.nowakArtur97.myMoments.postService.feature.resource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@ApiModel(description = "Details about the Comment")
class CommentModel {

    @ApiModelProperty(notes = "The unique id of the Comment")
    private String id;

    @ApiModelProperty(notes = "The comment's content")
    private String content;

    @ApiModelProperty(notes = "The comment's author")
    private String author;

    @ApiModelProperty(notes = "The comment's creation date")
    private LocalDateTime createDate;

    @ApiModelProperty(notes = "The comment's modification date")
    private LocalDateTime modifyDate;
}
