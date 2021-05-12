package com.nowakArtur97.myMoments.postService.feature.post;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class UserEventConsumer {

    @StreamListener(UserEventStream.INPUT)
    public void onMessage(Message<UserUpdateEventPayload> message) {

        UserUpdateEventPayload payload = message.getPayload();
    }
}
