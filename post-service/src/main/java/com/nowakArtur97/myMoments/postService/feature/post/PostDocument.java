package com.nowakArtur97.myMoments.postService.feature.post;

import com.nowakArtur97.myMoments.postService.common.document.AbstractDocument;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.Binary;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "post")
@AllArgsConstructor
@Getter
@Setter
@ToString
class PostDocument extends AbstractDocument {

    private String caption;

    private String username;

    private List<Binary> photos;
}
