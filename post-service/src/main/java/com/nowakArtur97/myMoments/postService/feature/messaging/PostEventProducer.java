package com.nowakArtur97.myMoments.postService.feature.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostEventProducer {

    private final PostEventStream userEventStream;

    public void sendPostDeleteEvent(String postIdPayload) {

        Message<String> message = MessageBuilder.withPayload(postIdPayload).build();

        userEventStream.postDeleteChannel().send(message);
    }
}
