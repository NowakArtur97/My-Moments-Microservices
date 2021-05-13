package com.nowakArtur97.myMoments.postService.feature.post;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class PostEventProducer {

    private final PostEventStream userEventStream;

    void sendPostDeleteEvent(String postIdPayload) {

        Message<String> message = MessageBuilder.withPayload(postIdPayload).build();

        userEventStream.postDeleteChannel().send(message);
    }
}
