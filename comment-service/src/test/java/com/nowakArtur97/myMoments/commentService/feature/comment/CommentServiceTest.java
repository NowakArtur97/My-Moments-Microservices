package com.nowakArtur97.myMoments.commentService.feature.comment;

import com.nowakArtur97.myMoments.commentService.feature.user.UserDocument;
import com.nowakArtur97.myMoments.commentService.feature.user.UserService;
import com.nowakArtur97.myMoments.commentService.feature.user.UserTestBuilder;
import com.nowakArtur97.myMoments.commentService.testUtil.enums.ObjectType;
import com.nowakArtur97.myMoments.commentService.testUtil.generator.NameWithSpacesGenerator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("CommentService_Tests")
class CommentServiceTest {

    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserService userService;

    private static MockedStatic<UUID> mocked;

    private static UserTestBuilder userTestBuilder;
    private static CommentTestBuilder commentTestBuilder;

    @BeforeAll
    static void setUpBuildersAndUUID() {

        userTestBuilder = new UserTestBuilder();
        commentTestBuilder = new CommentTestBuilder();

        UUID uuid = UUID.randomUUID();
        mocked = mockStatic(UUID.class);
        mocked.when(UUID::randomUUID).thenReturn(uuid);
    }

    @AfterAll
    static void cleanUp() {

        if (!mocked.isClosed()) {
            mocked.close();
        }
    }

    @BeforeEach
    void setUp() {

        commentService = new CommentService(commentRepository, userService);
    }

    @Nested
    class CreateCommentTest {

        @Test
        void when_create_comment_should_create_comment() {

            String postId = "postId";
            String authorExpected = "author";

            CommentDTO commentDTOExpected = (CommentDTO) commentTestBuilder.build(ObjectType.CREATE_DTO);
            CommentDocument commentExpected = (CommentDocument) commentTestBuilder.withRelatedPostId(postId)
                    .withAuthor(authorExpected).build(ObjectType.DOCUMENT);

            when(commentRepository.save(commentExpected)).thenReturn(Mono.just(commentExpected));

            Mono<CommentDocument> commentActualMono = commentService.addComment(postId, authorExpected,
                    commentDTOExpected);

            StepVerifier.create(commentActualMono)
                    .thenConsumeWhile(
                            commentActual -> {
                                assertAll(
                                        () -> verify(commentRepository, times(1)).save(commentExpected),
                                        () -> verifyNoMoreInteractions(commentRepository),
                                        () -> verifyNoInteractions(userService));
                                return assertComment(commentExpected, commentActual);
                            }
                    ).verifyComplete();
        }
    }

    @Nested
    class UpdateCommentTest {

        @Test
        void when_update_comment_should_update_comment() {

            String commentId = "commentId";
            String postId = "postId";
            String authorExpected = "author";
            String contentExpected = "updated content";

            CommentDTO commentDTOExpected = (CommentDTO) commentTestBuilder.withContent(contentExpected)
                    .build(ObjectType.CREATE_DTO);
            CommentDocument commentExpectedBeforeUpdate = (CommentDocument) commentTestBuilder.withId(commentId)
                    .withRelatedPostId(postId).withAuthor(authorExpected).build(ObjectType.DOCUMENT);
            UserDocument userExpected = userTestBuilder.withUsername(authorExpected).build();
            CommentDocument commentExpected = (CommentDocument) commentTestBuilder.withId(commentId)
                    .withContent(contentExpected).withRelatedPostId(postId).withAuthor(authorExpected)
                    .build(ObjectType.DOCUMENT);

            when(userService.findByUsername(authorExpected)).thenReturn(Mono.just(userExpected));
            when(commentRepository.findById(commentId)).thenReturn(Mono.just(commentExpectedBeforeUpdate));
            when(commentRepository.save(commentExpected)).thenReturn(Mono.just(commentExpected));

            Mono<CommentDocument> commentActualMono = commentService.updateComment(commentId, postId,
                    authorExpected, commentDTOExpected);

            StepVerifier.create(commentActualMono)
                    .thenConsumeWhile(
                            commentActual -> {
                                assertAll(
                                        () -> verify(userService, times(1))
                                                .findByUsername(authorExpected),
                                        () -> verifyNoMoreInteractions(userService),
                                        () -> verify(commentRepository, times(1))
                                                .findById(commentId),
                                        () -> verify(commentRepository, times(1)).save(commentExpected),
                                        () -> verifyNoMoreInteractions(commentRepository));
                                return assertComment(commentExpected, commentActual);
                            }
                    ).verifyComplete();
        }

        @Test
        void when_update_comment_of_not_existing_user_should_throw_exception() {

            String commentId = "commentId";
            String postId = "postId";
            String authorExpected = "author";

            CommentDTO commentDTOExpected = (CommentDTO) commentTestBuilder.build(ObjectType.CREATE_DTO);
            CommentDocument commentExpectedBeforeUpdate = (CommentDocument) commentTestBuilder.withId(commentId)
                    .withRelatedPostId(postId).withAuthor(authorExpected).build(ObjectType.DOCUMENT);

            when(userService.findByUsername(authorExpected)).thenReturn(Mono.empty());
            when(commentRepository.findById(commentId)).thenReturn(Mono.just(commentExpectedBeforeUpdate));

            Mono<CommentDocument> commentActualMono = commentService.updateComment(commentId, postId,
                    authorExpected, commentDTOExpected);

            StepVerifier.create(commentActualMono)
                    .then(() ->
                            assertAll(
                                    () -> verify(userService, times(1))
                                            .findByUsername(authorExpected),
                                    () -> verifyNoMoreInteractions(userService),
                                    () -> verify(commentRepository, times(1))
                                            .findById(commentId),
                                    () -> verifyNoMoreInteractions(commentRepository))
                    ).verifyErrorMessage("User with name: '" + authorExpected + "' not found.");
        }

        @Test
        void when_update_not_existing_comment_should_throw_exception() {

            String commentId = "commentId";
            String postId = "postId";
            String authorExpected = "author";
            String contentExpected = "updated content";

            CommentDTO commentDTOExpected = (CommentDTO) commentTestBuilder.withContent(contentExpected)
                    .build(ObjectType.CREATE_DTO);
            UserDocument userExpected = userTestBuilder.withUsername(authorExpected).build();

            when(userService.findByUsername(authorExpected)).thenReturn(Mono.just(userExpected));
            when(commentRepository.findById(commentId)).thenReturn(Mono.empty());

            Mono<CommentDocument> commentActualMono = commentService.updateComment(commentId, postId,
                    authorExpected, commentDTOExpected);

            StepVerifier.create(commentActualMono)
                    .then(() ->
                            assertAll(
                                    () -> verify(userService, times(1))
                                            .findByUsername(authorExpected),
                                    () -> verifyNoMoreInteractions(userService),
                                    () -> verify(commentRepository, times(1))
                                            .findById(commentId),
                                    () -> verifyNoMoreInteractions(commentRepository))
                    ).verifyErrorMessage("Comment with id: '" + commentId + "' not found.");
        }

        @Test
        void when_update_other_post_comment_should_throw_exception() {

            String commentId = "commentId";
            String postId = "postId";
            String otherPostId = "other postId";
            String authorExpected = "author";
            String contentExpected = "updated content";

            CommentDTO commentDTOExpected = (CommentDTO) commentTestBuilder.withContent(contentExpected)
                    .build(ObjectType.CREATE_DTO);
            CommentDocument commentExpectedBeforeUpdate = (CommentDocument) commentTestBuilder.withId(commentId)
                    .withRelatedPostId(otherPostId).withAuthor(authorExpected).build(ObjectType.DOCUMENT);
            UserDocument userExpected = userTestBuilder.withUsername(authorExpected).build();

            when(userService.findByUsername(authorExpected)).thenReturn(Mono.just(userExpected));
            when(commentRepository.findById(commentId)).thenReturn(Mono.just(commentExpectedBeforeUpdate));

            Mono<CommentDocument> commentActualMono = commentService.updateComment(commentId, postId,
                    authorExpected, commentDTOExpected);

            StepVerifier.create(commentActualMono)
                    .then(() ->
                            assertAll(
                                    () -> verify(userService, times(1))
                                            .findByUsername(authorExpected),
                                    () -> verifyNoMoreInteractions(userService),
                                    () -> verify(commentRepository, times(1))
                                            .findById(commentId),
                                    () -> verifyNoMoreInteractions(commentRepository))
                    ).verifyErrorMessage("Comment with commentId: '" + commentId + "' in the post with id: '"
                    + postId + "' not found.");
        }

        @Test
        void when_update_other_user_comment_should_throw_exception() {

            String commentId = "commentId";
            String postId = "postId";
            String authorExpected = "author";
            String otherUser = "other user";
            String contentExpected = "updated content";

            CommentDTO commentDTOExpected = (CommentDTO) commentTestBuilder.withContent(contentExpected)
                    .build(ObjectType.CREATE_DTO);
            CommentDocument commentExpectedBeforeUpdate = (CommentDocument) commentTestBuilder.withId(commentId)
                    .withRelatedPostId(postId).withAuthor(authorExpected).build(ObjectType.DOCUMENT);
            UserDocument userExpected = userTestBuilder.withUsername(otherUser).build();

            when(userService.findByUsername(authorExpected)).thenReturn(Mono.just(userExpected));
            when(commentRepository.findById(commentId)).thenReturn(Mono.just(commentExpectedBeforeUpdate));

            Mono<CommentDocument> commentActualMono = commentService.updateComment(commentId, postId,
                    authorExpected, commentDTOExpected);

            StepVerifier.create(commentActualMono)
                    .then(() ->
                            assertAll(
                                    () -> verify(userService, times(1))
                                            .findByUsername(authorExpected),
                                    () -> verifyNoMoreInteractions(userService),
                                    () -> verify(commentRepository, times(1))
                                            .findById(commentId),
                                    () -> verifyNoMoreInteractions(commentRepository))
                    ).verifyErrorMessage("User can only change his own comments.");
        }
    }

    @Nested
    class DeleteCommentTest {

        @Test
        void when_delete_comment_should_delete_comment() {

            String commentId = "commentId";
            String postId = "postId";
            String authorExpected = "author";

            UserDocument userExpected = userTestBuilder.withUsername(authorExpected).build();
            CommentDocument commentExpected = (CommentDocument) commentTestBuilder.withId(commentId)
                    .withRelatedPostId(postId).withAuthor(authorExpected).build(ObjectType.DOCUMENT);

            when(userService.findByUsername(authorExpected)).thenReturn(Mono.just(userExpected));
            when(commentRepository.findById(commentId)).thenReturn(Mono.just(commentExpected));
            when(commentRepository.delete(commentExpected)).thenReturn(Mono.empty());

            Mono<Void> commentVoidMono = commentService.deleteComment(commentId, postId,
                    authorExpected);

            StepVerifier.create(commentVoidMono)
                    .then(() ->
                            assertAll(
                                    () -> verify(userService, times(1))
                                            .findByUsername(authorExpected),
                                    () -> verifyNoMoreInteractions(userService),
                                    () -> verify(commentRepository, times(1)).findById(commentId),
                                    () -> verify(commentRepository, times(1))
                                            .delete(commentExpected),
                                    () -> verifyNoMoreInteractions(commentRepository))
                    ).verifyComplete();
        }

        @Test
        void when_delete_comment_of_not_existing_user_should_throw_exception() {

            String commentId = "commentId";
            String postId = "postId";
            String authorExpected = "author";

            CommentDocument commentExpected = (CommentDocument) commentTestBuilder.withId(commentId)
                    .withRelatedPostId(postId).withAuthor(authorExpected).build(ObjectType.DOCUMENT);

            when(userService.findByUsername(authorExpected)).thenReturn(Mono.empty());
            when(commentRepository.findById(commentId)).thenReturn(Mono.just(commentExpected));

            Mono<Void> commentVoidMono = commentService.deleteComment(commentId, postId,
                    authorExpected);

            StepVerifier.create(commentVoidMono)
                    .then(() ->
                            assertAll(
                                    () -> verify(userService, times(1))
                                            .findByUsername(authorExpected),
                                    () -> verifyNoMoreInteractions(userService),
                                    () -> verify(commentRepository, times(1))
                                            .findById(commentId),
                                    () -> verifyNoMoreInteractions(commentRepository))
                    ).verifyErrorMessage("User with name: '" + authorExpected + "' not found.");
        }

        @Test
        void when_delete_not_existing_comment_should_throw_exception() {

            String commentId = "commentId";
            String postId = "postId";
            String authorExpected = "author";

            UserDocument userExpected = userTestBuilder.withUsername(authorExpected).build();

            when(userService.findByUsername(authorExpected)).thenReturn(Mono.just(userExpected));
            when(commentRepository.findById(commentId)).thenReturn(Mono.empty());

            Mono<Void> commentVoidMono = commentService.deleteComment(commentId, postId,
                    authorExpected);

            StepVerifier.create(commentVoidMono)
                    .then(() ->
                            assertAll(
                                    () -> verify(userService, times(1))
                                            .findByUsername(authorExpected),
                                    () -> verifyNoMoreInteractions(userService),
                                    () -> verify(commentRepository, times(1))
                                            .findById(commentId),
                                    () -> verifyNoMoreInteractions(commentRepository))
                    ).verifyErrorMessage("Comment with id: '" + commentId + "' not found.");
        }

        @Test
        void when_delete_other_post_comment_should_throw_exception() {

            String commentId = "commentId";
            String postId = "postId";
            String otherPostId = "other postId";
            String authorExpected = "author";

            UserDocument userExpected = userTestBuilder.withUsername(authorExpected).build();
            CommentDocument commentExpected = (CommentDocument) commentTestBuilder.withId(commentId)
                    .withRelatedPostId(otherPostId).withAuthor(authorExpected).build(ObjectType.DOCUMENT);

            when(userService.findByUsername(authorExpected)).thenReturn(Mono.just(userExpected));
            when(commentRepository.findById(commentId)).thenReturn(Mono.just(commentExpected));

            Mono<Void> commentVoidMono = commentService.deleteComment(commentId, postId,
                    authorExpected);

            StepVerifier.create(commentVoidMono)
                    .then(() ->
                            assertAll(
                                    () -> verify(userService, times(1))
                                            .findByUsername(authorExpected),
                                    () -> verifyNoMoreInteractions(userService),
                                    () -> verify(commentRepository, times(1))
                                            .findById(commentId),
                                    () -> verifyNoMoreInteractions(commentRepository))
                    ).verifyErrorMessage("Comment with commentId: '" + commentId + "' in the post with id: '"
                    + postId + "' not found.");
        }

        @Test
        void when_delete_other_user_comment_should_throw_exception() {

            String commentId = "commentId";
            String postId = "postId";
            String authorExpected = "author";
            String otherUser = "other user";

            UserDocument userExpected = userTestBuilder.withUsername(otherUser).build();
            CommentDocument commentExpectedBeforeUpdate = (CommentDocument) commentTestBuilder.withId(commentId)
                    .withRelatedPostId(postId).withAuthor(authorExpected).build(ObjectType.DOCUMENT);

            when(userService.findByUsername(authorExpected)).thenReturn(Mono.just(userExpected));
            when(commentRepository.findById(commentId)).thenReturn(Mono.just(commentExpectedBeforeUpdate));

            Mono<Void> commentVoidMono = commentService.deleteComment(commentId, postId,
                    authorExpected);

            StepVerifier.create(commentVoidMono)
                    .then(() ->
                            assertAll(
                                    () -> verify(userService, times(1))
                                            .findByUsername(authorExpected),
                                    () -> verifyNoMoreInteractions(userService),
                                    () -> verify(commentRepository, times(1))
                                            .findById(commentId),
                                    () -> verifyNoMoreInteractions(commentRepository))
                    ).verifyErrorMessage("User can only change his own comments.");
        }
    }

    private boolean assertComment(CommentDocument commentExpected, CommentDocument commentActual) {

        assertAll(
                () -> assertEquals(commentExpected, commentActual,
                        () -> "should return comment: " + commentExpected + ", but was: " + commentActual),
                () -> assertEquals(commentExpected.getId(), commentActual.getId(),
                        () -> "should return comment with id: " + commentExpected.getId() + ", but was: "
                                + commentActual.getId()),
                () -> assertEquals(commentExpected.getRelatedPostId(), commentActual.getRelatedPostId(),
                        () -> "should return comment with post id: " + commentExpected.getRelatedPostId() + ", but was: "
                                + commentActual.getRelatedPostId()),
                () -> assertEquals(commentExpected.getAuthor(), commentActual.getAuthor(),
                        () -> "should return comment with author: " + commentExpected.getAuthor() + ", but was: "
                                + commentActual.getAuthor()),
                () -> assertEquals(commentExpected.getContent(), commentActual.getContent(),
                        () -> "should return comment with content: " + commentExpected.getContent() + ", but was: "
                                + commentActual.getContent()));
        return true;
    }
}
