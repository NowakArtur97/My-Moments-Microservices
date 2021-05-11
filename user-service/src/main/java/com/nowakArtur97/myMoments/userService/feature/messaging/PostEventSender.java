package com.nowakArtur97.myMoments.userService.feature.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PostEventSender {

    private PostEventStream channels;

    private final ObjectMapper objectMapper;

    public PostEventSender(PostEventStream channels, ObjectMapper objectMapper) {
        this.channels = channels;
        this.objectMapper = objectMapper;
    }

    public void sendPostCreated(UserUpdateEventPayload userUpdateEventPayload) throws JsonProcessingException {

        String jsonString = objectMapper.writeValueAsString(userUpdateEventPayload);

        Message<String> message =
                MessageBuilder
                        .withPayload(jsonString)
                        .build();

        channels.momentsPostChanged().send(message);
    }
}
