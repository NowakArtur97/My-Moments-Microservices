package com.nowakArtur97.myMoments.postService.feature.messaging;

import com.nowakArtur97.myMoments.postService.testUtil.generator.NameWithSpacesGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("PostEventProducer_Tests")
class PostEventProducerTest {

    private PostEventProducer postEventProducer;

    @Mock
    private PostEventStream postEventStream;

    @Mock
    private MessageChannel messageChannel;

    @BeforeEach
    void setUp() {

        postEventProducer = new PostEventProducer(postEventStream);
    }

    @Test
    void when_send_post_delete_event_should_send_event() {

        String postIdPayload = "postId";

        when(postEventStream.postDeleteChannel()).thenReturn(messageChannel);
        when(messageChannel.send(any(Message.class))).thenReturn(true);

        postEventProducer.sendPostDeleteEvent(postIdPayload);

        assertAll(() -> verify(messageChannel, times(1)).send(any(Message.class)),
                () -> verifyNoMoreInteractions(postEventStream));
    }
}
