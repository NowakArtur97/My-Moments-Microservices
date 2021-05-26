package com.nowakArtur97.myMoments.userService.feature.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserEventProducer {

    private final UserEventStream userEventStream;

    public void sendUserUpdateEvent(UserUpdateEventPayload userUpdateEventPayload) {

        if (shouldSendMessage(userUpdateEventPayload)) {

            log.info("Sending User Update Event for User: {}", userUpdateEventPayload.getNewUsername());

            Message<UserUpdateEventPayload> message = MessageBuilder.withPayload(userUpdateEventPayload).build();

            userEventStream.userUpdateChannel().send(message);
        }
    }

    private boolean shouldSendMessage(UserUpdateEventPayload userUpdateEventPayload) {
        return !userUpdateEventPayload.getNewUsername().equals(userUpdateEventPayload.getPreviousUsername());
    }

    public void sendUserDeleteEvent(String usernamePayload) {

        Message<String> message = MessageBuilder.withPayload(usernamePayload).build();

        log.info("Sending User Delete Event for User: {}", usernamePayload);

        userEventStream.userDeleteChannel().send(message);
    }
}
