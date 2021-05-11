package com.nowakArtur97.myMoments.postService.feature.post;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface PostEventStream {

    String INPUT = "momentsPostChanged";

    @Input(INPUT)
    SubscribableChannel momentsPostChanged();
}
