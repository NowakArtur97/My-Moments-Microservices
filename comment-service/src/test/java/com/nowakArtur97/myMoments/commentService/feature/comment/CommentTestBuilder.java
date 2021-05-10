package com.nowakArtur97.myMoments.commentService.feature.comment;

import com.nowakArtur97.myMoments.commentService.testUtil.enums.ObjectType;

import java.time.LocalDateTime;
import java.util.UUID;

class CommentTestBuilder {

    private String id = UUID.randomUUID().toString();

    private String content = "content";

    private String author;

    private String relatedPostId;

    private LocalDateTime createDate = LocalDateTime.now();

    private LocalDateTime modifyDate = LocalDateTime.now();

    CommentTestBuilder withId(String id) {

        this.id = id;

        return this;
    }

    CommentTestBuilder withContent(String content) {

        this.content = content;

        return this;
    }

    CommentTestBuilder withAuthor(String author) {

        this.author = author;

        return this;
    }

    CommentTestBuilder withRelatedPostId(String relatedPostId) {

        this.relatedPostId = relatedPostId;

        return this;
    }

    Comment build(ObjectType type) {

        Comment comment;

        switch (type) {

            case CREATE_DTO:
            case UPDATE_DTO:

                comment = new CommentDTO(content);

                break;

            case DOCUMENT:

                comment = new CommentDocument(content, author, relatedPostId);

                break;

            case MODEL:

                comment = new CommentModel(id, content, author, createDate, modifyDate);

                break;

            default:
                throw new RuntimeException("The specified type does not exist");
        }

        resetProperties();

        return comment;
    }

    private void resetProperties() {

        id = UUID.randomUUID().toString();

        content = "content";

        author = null;

        relatedPostId = null;

        createDate = LocalDateTime.now();

        modifyDate = LocalDateTime.now();
    }
}
