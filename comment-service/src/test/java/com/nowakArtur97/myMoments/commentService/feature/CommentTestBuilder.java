package com.nowakArtur97.myMoments.commentService.feature;

import com.nowakArtur97.myMoments.commentService.feature.document.Comment;
import com.nowakArtur97.myMoments.commentService.feature.document.CommentDocument;
import com.nowakArtur97.myMoments.commentService.feature.resource.CommentDTO;
import com.nowakArtur97.myMoments.commentService.feature.resource.CommentModel;
import com.nowakArtur97.myMoments.commentService.testUtil.enums.ObjectType;

import java.util.Date;
import java.util.UUID;

public class CommentTestBuilder {

    private String id = UUID.randomUUID().toString();

    private String content = "content";

    private String author;

    private String relatedPostId;

    private Date createDate = new Date();

    private Date modifyDate = new Date();

    public CommentTestBuilder withId(String id) {

        this.id = id;

        return this;
    }

    public CommentTestBuilder withContent(String content) {

        this.content = content;

        return this;
    }

    public CommentTestBuilder withAuthor(String author) {

        this.author = author;

        return this;
    }

    public CommentTestBuilder withRelatedPostId(String relatedPostId) {

        this.relatedPostId = relatedPostId;

        return this;
    }

    public Comment build(ObjectType type) {

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

        createDate = new Date();

        modifyDate = new Date();
    }
}
