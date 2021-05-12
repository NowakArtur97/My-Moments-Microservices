package com.nowakArtur97.myMoments.postService.feature.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventConsumer {

    private final ObjectMapper objectMapper;

    @StreamListener(UserEventStream.INPUT)
    public void onMessage(Message<byte[]> message) throws IOException {

        UserUpdateEventPayload payload = objectMapper.readValue(message.getPayload(), UserUpdateEventPayload.class);

        log.info(payload.toString());
    }
}
