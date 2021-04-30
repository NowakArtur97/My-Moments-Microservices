package com.nowakArtur97.myMoments.postService.feature.post;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.http.codec.multipart.FilePart;

import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@ApiModel(description = "Model responsible for Post's validation")
class PostDTO implements Post {

    @Size(max = 1000, message = "{post.caption.size}")
    @ApiModelProperty(notes = "The post's caption")
    private String caption;

    @NotEmptyMultipartList(message = "{post.photos.notEmpty}")
    @Size(min = 1, max = 10, message = "{post.photos.size}")
    @ApiModelProperty(notes = "The post's photos")
    private List<FilePart> photos;
}
