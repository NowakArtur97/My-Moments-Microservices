package com.nowakArtur97.myMoments.userService.feature.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserEventProducer {

    private final UserEventStream channels;

    public void sendUserUpdateEvent(UserUpdateEventPayload userUpdateEventPayload) {

        Message<UserUpdateEventPayload> message = MessageBuilder.withPayload(userUpdateEventPayload).build();

        channels.userChangedChannel().send(message);
    }
}
