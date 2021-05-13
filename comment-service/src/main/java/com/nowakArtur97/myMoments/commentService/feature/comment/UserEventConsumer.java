package com.nowakArtur97.myMoments.commentService.feature.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class UserEventConsumer {

    @StreamListener(UserEventStream.UPDATE_INPUT)
    public void onUserUpdateMessage(Message<UserUpdateEventPayload> message) {

        UserUpdateEventPayload payload = message.getPayload();

        log.info(payload.toString());
    }

    @StreamListener(UserEventStream.DELETE_INPUT)
    public void onUserDeleteMessage(Message<String> message) {

        String usernamePayload = message.getPayload();

        log.info(usernamePayload);
    }
}
