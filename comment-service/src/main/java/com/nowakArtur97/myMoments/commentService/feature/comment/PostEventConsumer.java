package com.nowakArtur97.myMoments.commentService.feature.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class PostEventConsumer {
    
    @StreamListener(PostEventStream.DELETE_INPUT)
    public void onPostDeleteMessage(Message<String> message) {

        String postIdPayload = message.getPayload();

        log.info(postIdPayload);
    }
}
