package com.nowakArtur97.myMoments.postService.feature.post;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@ToString
@ApiModel(description = "Details about the Post")
class PostModel implements Post {

    @ApiModelProperty(notes = "The unique id of the Post")
    private String id;

    @ApiModelProperty(notes = "The post's caption")
    private String caption;

    @ApiModelProperty(notes = "The post's author")
    private String author;

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
