package com.nowakArtur97.myMoments.postService.feature.resource;

import com.nowakArtur97.myMoments.postService.feature.document.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;
import java.util.Objects;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Schema(description = "Details about the Post")
public class PostModel implements Post {

    @Schema(accessMode = READ_ONLY, description = "The unique id of the Post")
    protected String id;

    @Schema(accessMode = READ_ONLY, description = "The post's caption")
    protected String caption;

    @Schema(accessMode = READ_ONLY, description = "The post's author")
    protected String author;

    @Schema(accessMode = READ_ONLY, description = "The post's photos")
    protected List<byte[]> photos;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof PostModel)) return false;

        PostModel postModel = (PostModel) o;

        return Objects.equals(getId(), postModel.getId()) && Objects.equals(getCaption(), postModel.getCaption());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getCaption());
    }
}
