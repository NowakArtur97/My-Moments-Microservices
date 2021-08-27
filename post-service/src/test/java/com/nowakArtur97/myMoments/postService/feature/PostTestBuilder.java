package com.nowakArtur97.myMoments.postService.feature;

import com.nowakArtur97.myMoments.postService.feature.document.Post;
import com.nowakArtur97.myMoments.postService.feature.document.PostDocument;
import com.nowakArtur97.myMoments.postService.feature.resource.CommentModel;
import com.nowakArtur97.myMoments.postService.feature.resource.PostDTO;
import com.nowakArtur97.myMoments.postService.feature.resource.PostModel;
import com.nowakArtur97.myMoments.postService.feature.resource.PostModelWithComments;
import com.nowakArtur97.myMoments.postService.testUtil.enums.ObjectType;
import org.bson.types.Binary;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PostTestBuilder {

    private String id = UUID.randomUUID().toString();

    private String caption = "caption";

    private String author = "author";

    private List<Binary> photosBinary = new ArrayList<>();

    private List<byte[]> photosBytes = new ArrayList<>();

    private List<CommentModel> comments = new ArrayList<>();

    public PostTestBuilder withId(String id) {

        this.id = id;

        return this;
    }

    public PostTestBuilder withCaption(String caption) {

        this.caption = caption;

        return this;
    }

    public PostTestBuilder withAuthor(String author) {

        this.author = author;

        return this;
    }

    public PostTestBuilder withBinary(List<Binary> photos) {

        this.photosBinary = photos;

        return this;
    }

    public PostTestBuilder withBytes(List<byte[]> photos) {

        this.photosBytes = photos;

        return this;
    }

    public PostTestBuilder withComments(List<CommentModel> comments) {

        this.comments = comments;

        return this;
    }

    public Post build(ObjectType type) {

        Post post;

        switch (type) {

            case CREATE_DTO:

                post = new PostDTO(caption, photosBinary);

                break;

            case DOCUMENT:

                post = new PostDocument(caption, author, photosBinary);

                break;

            case MODEL:

                if (comments == null || comments.isEmpty()) {
                    post = new PostModel(id, caption, author, photosBytes);
                } else {
                    post = new PostModelWithComments(id, caption, author, photosBytes, comments);
                }

                break;

            default:
                throw new RuntimeException("The specified type does not exist");
        }

        resetProperties();

        return post;
    }

    private void resetProperties() {

        id = UUID.randomUUID().toString();

        caption = "caption";

        author = "author";

        photosBinary = new ArrayList<>();

        photosBytes = new ArrayList<>();

        comments = new ArrayList<>();
    }
}
