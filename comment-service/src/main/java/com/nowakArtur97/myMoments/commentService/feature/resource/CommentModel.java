package com.nowakArtur97.myMoments.commentService.feature.resource;

import com.nowakArtur97.myMoments.commentService.feature.document.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Date;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Schema(description = "Details about the Comment")
public class CommentModel implements Comment {

    @Schema(accessMode = READ_ONLY, description = "The unique id of the Comment")
    private String id;

    @Schema(accessMode = READ_ONLY, description = "The comment's content")
    private String content;

    @Schema(accessMode = READ_ONLY, description = "The comment's author")
    private String author;

    @Schema(accessMode = READ_ONLY, description = "The comment's creation date")
    private Date createDate;

    @Schema(accessMode = READ_ONLY, description = "The comment's modification date")
    private Date modifyDate;
}
