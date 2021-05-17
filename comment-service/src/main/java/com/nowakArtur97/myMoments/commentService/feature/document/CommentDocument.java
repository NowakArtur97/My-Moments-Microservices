package com.nowakArtur97.myMoments.commentService.feature.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "comment")
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CommentDocument extends AbstractDocument implements Comment {

    private String content;

    private String author;

    private String relatedPostId;
}
