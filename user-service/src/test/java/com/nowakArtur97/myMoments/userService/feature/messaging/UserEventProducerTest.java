package com.nowakArtur97.myMoments.userService.feature.messaging;

import com.nowakArtur97.myMoments.userService.testUtil.generator.NameWithSpacesGenerator;
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
@Tag("UserEventProducer_Tests")
public class UserEventProducerTest {

    private UserEventProducer userEventProducer;

    @Mock
    private UserEventStream userEventStream;

    @Mock
    private MessageChannel messageChannel;

    @BeforeEach
    void setUp() {

        userEventProducer = new UserEventProducer(userEventStream);
    }

    @Test
    void when_send_user_update_event_with_updated_username_should_send_event() {

        UserUpdateEventPayload userUpdateEventPayload =
                new UserUpdateEventPayload("previousUsername", "newUsername");

        when(userEventStream.userChangedChannel()).thenReturn(messageChannel);
        when(messageChannel.send(any(Message.class))).thenReturn(true);

        userEventProducer.sendUserUpdateEvent(userUpdateEventPayload);

        assertAll(() -> verify(messageChannel, times(1)).send(any(Message.class)),
                () -> verifyNoMoreInteractions(userEventStream));
    }

    @Test
    void when_send_user_update_event_without_updated_username_should_not_send_event() {

        UserUpdateEventPayload userUpdateEventPayload =
                new UserUpdateEventPayload("sameUsername", "sameUsername");

        userEventProducer.sendUserUpdateEvent(userUpdateEventPayload);

        assertAll(() -> verifyNoInteractions(userEventStream));
    }
}
