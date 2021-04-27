package com.nowakArtur97.myMoments.postService.feature.post.document;

import com.nowakArtur97.myMoments.postService.common.document.AbstractDocument;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "post")
@AllArgsConstructor
@Getter
@Setter
@ToString
public class PostDocument extends AbstractDocument {

    private String caption;

    private String username;
}
