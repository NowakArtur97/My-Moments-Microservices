package com.nowakArtur97.myMoments.commentService.feature.comment;

import com.nowakArtur97.myMoments.commentService.testUtil.enums.ObjectType;
import com.nowakArtur97.myMoments.commentService.testUtil.generator.NameWithSpacesGenerator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("UserEventConsumer_Tests")
class UserEventConsumerTest {

    private UserEventConsumer userEventConsumer;

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

        userEventConsumer = new UserEventConsumer(commentRepository);
    }

    @Test
    void when_update_user_event_should_update_comments_authors() {

        UserUpdateEventPayload userUpdateEventPayload
                = new UserUpdateEventPayload("previousUsername", "newUsername");
        Message<UserUpdateEventPayload> message = MessageBuilder.withPayload(userUpdateEventPayload).build();
        CommentDocument commentDocumentExpectedBeforeUpdate1 = (CommentDocument) commentTestBuilder
                .withAuthor(userUpdateEventPayload.getPreviousUsername()).build(ObjectType.DOCUMENT);
        CommentDocument commentDocumentExpectedBeforeUpdate2 = (CommentDocument) commentTestBuilder
                .withAuthor(userUpdateEventPayload.getPreviousUsername()).build(ObjectType.DOCUMENT);

        CommentDocument commentDocumentExpected1 = (CommentDocument) commentTestBuilder
                .withAuthor(userUpdateEventPayload.getNewUsername()).build(ObjectType.DOCUMENT);
        CommentDocument commentDocumentExpected2 = (CommentDocument) commentTestBuilder
                .withAuthor(userUpdateEventPayload.getNewUsername()).build(ObjectType.DOCUMENT);

        when(commentRepository.findByAuthor(userUpdateEventPayload.getPreviousUsername()))
                .thenReturn(Flux.just(commentDocumentExpectedBeforeUpdate1, commentDocumentExpectedBeforeUpdate2));
        when(commentRepository.save(commentDocumentExpected1)).thenReturn(Mono.just(commentDocumentExpected1));
        when(commentRepository.save(commentDocumentExpected2)).thenReturn(Mono.just(commentDocumentExpected2));

        userEventConsumer.onUserUpdateMessage(message);

        assertAll(() -> verify(commentRepository, times(1))
                        .findByAuthor(userUpdateEventPayload.getPreviousUsername()),
                () -> verify(commentRepository, times(2)).save(commentDocumentExpected1),
                () -> verify(commentRepository, times(2)).save(commentDocumentExpected2),
                () -> verifyNoMoreInteractions(commentRepository));
    }

    @Test
    void when_delete_user_event_should_delete_comments_by_author() {

        String authorPayload = "author";
        Message<String> message = MessageBuilder.withPayload(authorPayload).build();

        when(commentRepository.deleteByAuthor(authorPayload)).thenReturn(Mono.empty());

        userEventConsumer.onUserDeleteMessage(message);

        assertAll(() -> verify(commentRepository, times(1))
                        .deleteByAuthor(authorPayload),
                () -> verifyNoMoreInteractions(commentRepository));
    }
}