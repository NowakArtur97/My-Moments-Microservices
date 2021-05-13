package com.nowakArtur97.myMoments.commentService.feature.comment;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface PostEventStream {
    
    String DELETE_INPUT = "postDeleteChannel";

    @Input(DELETE_INPUT)
    SubscribableChannel postDeleteChannel();
}
