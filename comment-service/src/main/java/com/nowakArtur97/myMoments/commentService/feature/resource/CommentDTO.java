package com.nowakArtur97.myMoments.commentService.feature.resource;

import com.nowakArtur97.myMoments.commentService.feature.document.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Schema(description = "Model responsible for Comment's validation")
public class CommentDTO implements Comment {

    @NotBlank(message = "{comment.content.notBlank}")
    @Size(max = 200, message = "{comment.content.size}")
    @Schema(accessMode = READ_ONLY, description = "The comment's content")
    private String content;
}

