package com.nowakArtur97.myMoments.commentService.feature.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@ApiModel(description = "Details about the Comment")
class CommentModel implements Comment {

    @ApiModelProperty(notes = "The unique id of the Comment")
    private String id;

    @ApiModelProperty(notes = "The comment's content")
    private String content;

    @ApiModelProperty(notes = "The comment's author")
    private String author;

    @ApiModelProperty(notes = "The comment's creation date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Europe/Warsaw")
    private Date createDate;

    @ApiModelProperty(notes = "The comment's modification date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Europe/Warsaw")
    private Date modifyDate;
}
