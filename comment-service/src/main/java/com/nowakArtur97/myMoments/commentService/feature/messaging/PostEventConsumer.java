package com.nowakArtur97.myMoments.commentService.feature.messaging;

import com.nowakArtur97.myMoments.commentService.feature.document.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class PostEventConsumer {

    private final CommentRepository commentRepository;

    @StreamListener(PostEventStream.DELETE_INPUT)
    public void onPostDeleteMessage(Message<String> message) {

        String postIdPayload = message.getPayload();

        commentRepository.deleteByRelatedPostId(postIdPayload)
                .subscribe();
    }
}
