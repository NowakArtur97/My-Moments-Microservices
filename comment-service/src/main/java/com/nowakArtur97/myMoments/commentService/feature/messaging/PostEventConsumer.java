package com.nowakArtur97.myMoments.commentService.feature.messaging;

import com.nowakArtur97.myMoments.commentService.feature.document.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class PostEventConsumer {

    private final CommentRepository commentRepository;

    @StreamListener(PostEventStream.DELETE_INPUT)
    public void onPostDeleteMessage(Message<String> message) {

        String postIdPayload = message.getPayload();

        log.info("Consuming Post Delete Event with id: {}", postIdPayload);

        commentRepository.deleteByRelatedPost(postIdPayload)
                .subscribe();
    }
}
