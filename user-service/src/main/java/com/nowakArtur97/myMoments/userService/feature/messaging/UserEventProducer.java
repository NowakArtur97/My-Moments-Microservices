package com.nowakArtur97.myMoments.userService.feature.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserEventProducer {

    private final UserEventStream channels;

    private final ObjectMapper objectMapper;

    public void sendPostCreated(UserUpdateEventPayload userUpdateEventPayload) throws JsonProcessingException {

        String payloadAsString = objectMapper.writeValueAsString(userUpdateEventPayload);

        Message<String> message = MessageBuilder.withPayload(payloadAsString).build();

        channels.userChangedChannel().send(message);
    }
}
