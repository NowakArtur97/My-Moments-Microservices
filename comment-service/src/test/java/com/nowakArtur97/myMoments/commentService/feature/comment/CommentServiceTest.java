package com.nowakArtur97.myMoments.commentService.feature.comment;

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

    private static MockedStatic<UUID> mocked;

    private static CommentTestBuilder commentTestBuilder;

    @BeforeAll
    static void setUpBuildersAndUUID() {

        commentTestBuilder = new CommentTestBuilder();

        UUID uuid = UUID.randomUUID();
        mocked = mockStatic(UUID.class);
        mocked.when(UUID::randomUUID).thenReturn(uuid);
    }

    @BeforeEach
    void setUp() {

        commentService = new CommentService(commentRepository);
    }

    @Nested
    class CreateCommentTest {

        @Test
        void when_create_comment_should_create_comment() {

            String relatedPostIdExpected = "postId";
            String authorExpected = "author";

            CommentDTO commentDTOExpected = (CommentDTO) commentTestBuilder.build(ObjectType.CREATE_DTO);
            CommentDocument commentExpected = (CommentDocument) commentTestBuilder.withRelatedPostId(relatedPostIdExpected)
                    .withAuthor(authorExpected).build(ObjectType.DOCUMENT);

            when(commentRepository.save(commentExpected)).thenReturn(Mono.just(commentExpected));

            Mono<CommentDocument> commentActualMono = commentService.addComment(relatedPostIdExpected, authorExpected,
                    commentDTOExpected);

            StepVerifier.create(commentActualMono)
                    .thenConsumeWhile(
                            commentActual -> {
                                assertAll(() -> verify(commentRepository, times(1)).save(commentExpected),
                                        () -> verifyNoMoreInteractions(commentRepository));
                                return assertComment(commentExpected, commentActual);
                            }
                    ).verifyComplete();
        }
    }

    private boolean assertComment(CommentDocument commentExpected, CommentDocument commentActual) {

        assertAll(() -> assertEquals(commentExpected, commentActual,
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
