package com.nowakArtur97.myMoments.postService.feature.post.document;

import com.nowakArtur97.myMoments.postService.common.document.AbstractDocument;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "post")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
class PostDocument extends AbstractDocument {

    private String caption;

    private String username;
}
