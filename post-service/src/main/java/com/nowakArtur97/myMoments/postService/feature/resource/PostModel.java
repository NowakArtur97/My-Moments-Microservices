package com.nowakArtur97.myMoments.postService.feature.resource;

import com.nowakArtur97.myMoments.postService.feature.document.Post;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.bson.types.Binary;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@ApiModel(description = "Details about the Post")
public class PostModel implements Post {

    @ApiModelProperty(notes = "The unique id of the Post")
    protected String id;

    @ApiModelProperty(notes = "The post's caption")
    protected String caption;

    @ApiModelProperty(notes = "The post's author")
    protected String author;

    @ApiModelProperty(notes = "The post's photos")
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
