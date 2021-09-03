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
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("UserEventProducer_Tests")
class UserEventProducerTest {

    private static final String DEV_PROFILE = "dev";
    private static final String LOCAL_PROFILE = "local";

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
    void when_send_user_update_event_with_updated_username_and_profile_is_not_dev_should_send_event() {

        ReflectionTestUtils.setField(userEventProducer, "activeProfile", LOCAL_PROFILE);

        UserUpdateEventPayload userUpdateEventPayload =
                new UserUpdateEventPayload("previousUsername", "newUsername");

        when(userEventStream.userUpdateChannel()).thenReturn(messageChannel);
        when(messageChannel.send(any(Message.class))).thenReturn(true);

        userEventProducer.sendUserUpdateEvent(userUpdateEventPayload);

        assertAll(() -> verify(messageChannel, times(1)).send(any(Message.class)),
                () -> verifyNoMoreInteractions(userEventStream));
    }

    @Test
    void when_send_user_update_event_with_updated_username_and_profile_is_dev_should_not_send_event() {

        ReflectionTestUtils.setField(userEventProducer, "activeProfile", DEV_PROFILE);

        UserUpdateEventPayload userUpdateEventPayload =
                new UserUpdateEventPayload("previousUsername", "newUsername");

        userEventProducer.sendUserUpdateEvent(userUpdateEventPayload);

        assertAll(() -> verifyNoInteractions(userEventStream));
    }

    @Test
    void when_send_user_update_event_without_updated_username_and_profile_is_not_dev_should_not_send_event() {

        ReflectionTestUtils.setField(userEventProducer, "activeProfile", LOCAL_PROFILE);

        UserUpdateEventPayload userUpdateEventPayload =
                new UserUpdateEventPayload("sameUsername", "sameUsername");

        userEventProducer.sendUserUpdateEvent(userUpdateEventPayload);

        assertAll(() -> verifyNoInteractions(userEventStream));
    }

    @Test
    void when_send_user_delete_event_and_profile_is_not_dev_should_send_event() {

        ReflectionTestUtils.setField(userEventProducer, "activeProfile", LOCAL_PROFILE);

        String usernamePayload = "username";

        when(userEventStream.userDeleteChannel()).thenReturn(messageChannel);
        when(messageChannel.send(any(Message.class))).thenReturn(true);

        userEventProducer.sendUserDeleteEvent(usernamePayload);

        assertAll(() -> verify(messageChannel, times(1)).send(any(Message.class)),
                () -> verifyNoMoreInteractions(userEventStream));
    }

    @Test
    void when_send_user_delete_event_and_profile_is_dev_should_not_send_event() {

        ReflectionTestUtils.setField(userEventProducer, "activeProfile", DEV_PROFILE);

        String usernamePayload = "username";

        userEventProducer.sendUserDeleteEvent(usernamePayload);

        assertAll(() -> verifyNoInteractions(userEventStream));
    }
}
