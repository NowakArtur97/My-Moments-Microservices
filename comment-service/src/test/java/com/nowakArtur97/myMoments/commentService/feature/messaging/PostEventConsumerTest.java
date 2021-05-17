package com.nowakArtur97.myMoments.commentService.feature.messaging;

import com.nowakArtur97.myMoments.commentService.feature.document.CommentRepository;
import com.nowakArtur97.myMoments.commentService.testUtil.generator.NameWithSpacesGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("PostEventConsumer_Tests")
class PostEventConsumerTest {

    private PostEventConsumer postEventConsumer;

    @Mock
    private CommentRepository commentRepository;

    @BeforeEach
    void setUp() {

        postEventConsumer = new PostEventConsumer(commentRepository);
    }

    @Test
    void when_delete_post_event_should_delete_comments_by_related_post_id() {

        String relatedPostIdPayload = "postId";
        Message<String> message = MessageBuilder.withPayload(relatedPostIdPayload).build();

        when(commentRepository.deleteByRelatedPostId(relatedPostIdPayload)).thenReturn(Mono.empty());

        postEventConsumer.onPostDeleteMessage(message);

        assertAll(() -> verify(commentRepository, times(1))
                        .deleteByRelatedPostId(relatedPostIdPayload),
                () -> verifyNoMoreInteractions(commentRepository));
    }
}