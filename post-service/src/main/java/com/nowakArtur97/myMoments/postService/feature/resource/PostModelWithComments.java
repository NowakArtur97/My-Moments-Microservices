package com.nowakArtur97.myMoments.postService.feature.resource;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Schema(description = "Details about the Post")
public class PostModelWithComments extends PostModel {

    @Schema(accessMode = READ_ONLY, description = "The post's comments")
    private List<CommentModel> comments;

    public PostModelWithComments(String id, String caption, String author, List<byte[]> photos, List<CommentModel> comments) {
        super(id, caption, author, photos);
        this.comments = comments;
    }
}
