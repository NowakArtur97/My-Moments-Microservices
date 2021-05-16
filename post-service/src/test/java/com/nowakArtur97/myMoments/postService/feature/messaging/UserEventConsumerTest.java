package com.nowakArtur97.myMoments.postService.feature.messaging;

import com.nowakArtur97.myMoments.postService.feature.PostTestBuilder;
import com.nowakArtur97.myMoments.postService.feature.document.PostDocument;
import com.nowakArtur97.myMoments.postService.feature.document.PostRepository;
import com.nowakArtur97.myMoments.postService.testUtil.enums.ObjectType;
import com.nowakArtur97.myMoments.postService.testUtil.generator.NameWithSpacesGenerator;
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
    private PostRepository postRepository;

    private static MockedStatic<UUID> mocked;

    private static PostTestBuilder postTestBuilder;

    @BeforeAll
    static void setUpBuilderAndUUID() {

        postTestBuilder = new PostTestBuilder();

        UUID uuid = UUID.randomUUID();
        mocked = mockStatic(UUID.class);
        mocked.when(UUID::randomUUID).thenReturn(uuid);
    }

    @BeforeEach
    void setUp() {

        userEventConsumer = new UserEventConsumer(postRepository);
    }

    @AfterAll
    static void cleanUp() {

        if (!mocked.isClosed()) {
            mocked.close();
        }
    }

    @Test
    void when_update_user_event_should_update_posts_authors() {

        UserUpdateEventPayload userUpdateEventPayload
                = new UserUpdateEventPayload("previousUsername", "newUsername");
        Message<UserUpdateEventPayload> message = MessageBuilder.withPayload(userUpdateEventPayload).build();
        PostDocument postDocumentExpectedBeforeUpdate1 = (PostDocument) postTestBuilder
                .withAuthor(userUpdateEventPayload.getPreviousUsername()).build(ObjectType.DOCUMENT);
        PostDocument postDocumentExpectedBeforeUpdate2 = (PostDocument) postTestBuilder
                .withAuthor(userUpdateEventPayload.getPreviousUsername()).build(ObjectType.DOCUMENT);

        PostDocument postDocumentExpected1 = (PostDocument) postTestBuilder
                .withAuthor(userUpdateEventPayload.getNewUsername()).build(ObjectType.DOCUMENT);
        PostDocument postDocumentExpected2 = (PostDocument) postTestBuilder
                .withAuthor(userUpdateEventPayload.getNewUsername()).build(ObjectType.DOCUMENT);

        when(postRepository.findByAuthor(userUpdateEventPayload.getPreviousUsername()))
                .thenReturn(Flux.just(postDocumentExpectedBeforeUpdate1, postDocumentExpectedBeforeUpdate2));
        when(postRepository.save(postDocumentExpected1)).thenReturn(Mono.just(postDocumentExpected1));
        when(postRepository.save(postDocumentExpected2)).thenReturn(Mono.just(postDocumentExpected2));

        userEventConsumer.onUserUpdateMessage(message);

        assertAll(() -> verify(postRepository, times(1))
                        .findByAuthor(userUpdateEventPayload.getPreviousUsername()),
                () -> verify(postRepository, times(2)).save(postDocumentExpected1),
                () -> verify(postRepository, times(2)).save(postDocumentExpected2),
                () -> verifyNoMoreInteractions(postRepository));
    }

    @Test
    void when_delete_user_event_should_delete_posts_by_author() {

        String authorPayload = "author";
        Message<String> message = MessageBuilder.withPayload(authorPayload).build();

        when(postRepository.deleteByAuthor(authorPayload)).thenReturn(Mono.empty());

        userEventConsumer.onUserDeleteMessage(message);

        assertAll(() -> verify(postRepository, times(1))
                        .deleteByAuthor(authorPayload),
                () -> verifyNoMoreInteractions(postRepository));
    }
}