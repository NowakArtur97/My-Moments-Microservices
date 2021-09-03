package com.nowakArtur97.myMoments.postService.feature.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostEventProducer {

    private static final String DEV_PROFILE = "dev";

    @Value("${spring.profiles.active}")
    private String activeProfile;

    private final PostEventStream userEventStream;

    public void sendPostDeleteEvent(String postIdPayload) {

        if (!activeProfile.equals(DEV_PROFILE)) {

            Message<String> message = MessageBuilder.withPayload(postIdPayload).build();

            log.info("Sending Post Delete Event with id: {}", postIdPayload);

            userEventStream.postDeleteChannel().send(message);
        }
    }
}
