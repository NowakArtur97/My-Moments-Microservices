package com.nowakArtur97.myMoments.postService.feature.resource;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Schema(description = "Details about the Comment")
public class CommentModel {

    @Schema(accessMode = READ_ONLY, description = "The unique id of the Comment")
    private String id;

    @Schema(accessMode = READ_ONLY, description = "The comment's content")
    private String content;

    @Schema(accessMode = READ_ONLY, description = "The comment's author")
    private String author;

    @Schema(accessMode = READ_ONLY, description = "The comment's creation date")
    private LocalDateTime createDate;

    @Schema(accessMode = READ_ONLY, description = "The comment's modification date")
    private LocalDateTime modifyDate;
}
