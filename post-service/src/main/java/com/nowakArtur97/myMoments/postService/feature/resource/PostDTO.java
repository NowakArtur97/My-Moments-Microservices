package com.nowakArtur97.myMoments.postService.feature.resource;

import com.nowakArtur97.myMoments.postService.feature.document.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.bson.types.Binary;

import javax.validation.constraints.Size;
import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Schema(description = "Model responsible for Post's validation")
public class PostDTO implements Post {

    @Size(max = 1000, message = "{post.caption.size}")
    @Schema(accessMode = READ_ONLY, description = "The post's caption")
    private String caption;

    @Size(min = 1, max = 10, message = "{post.photos.size}")
    @Schema(accessMode = READ_ONLY, description = "The post's photos")
    private List<Binary> photos;
}
