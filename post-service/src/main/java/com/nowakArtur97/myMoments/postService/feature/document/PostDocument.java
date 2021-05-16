package com.nowakArtur97.myMoments.postService.feature.document;

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
public class PostDocument extends AbstractDocument implements Post {

    private String caption;

    private String author;

    private List<Binary> photos;
}
