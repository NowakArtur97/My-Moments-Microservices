package com.nowakArtur97.myMoments.userService.feature.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserEventProducer {

    private final UserEventStream userEventStream;

    public void sendUserUpdateEvent(UserUpdateEventPayload userUpdateEventPayload) {

        if (shouldSendMessage(userUpdateEventPayload)) {

            Message<UserUpdateEventPayload> message = MessageBuilder.withPayload(userUpdateEventPayload).build();

            userEventStream.userChangedChannel().send(message);
        }
    }

    private boolean shouldSendMessage(UserUpdateEventPayload userUpdateEventPayload) {
        return !userUpdateEventPayload.getNewUsername().equals(userUpdateEventPayload.getPreviousUsername());
    }
}
