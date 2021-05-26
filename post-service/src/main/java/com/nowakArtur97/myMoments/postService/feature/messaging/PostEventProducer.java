package com.nowakArtur97.myMoments.postService.feature.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostEventProducer {

    private final PostEventStream userEventStream;

    public void sendPostDeleteEvent(String postIdPayload) {

        log.info("Sending Post Delete Event with id: {}", postIdPayload);

        Message<String> message = MessageBuilder.withPayload(postIdPayload).build();

        userEventStream.postDeleteChannel().send(message);
    }
}
