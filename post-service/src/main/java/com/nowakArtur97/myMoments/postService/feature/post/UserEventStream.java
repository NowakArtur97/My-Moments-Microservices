package com.nowakArtur97.myMoments.postService.feature.post;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface UserEventStream {

    String INPUT = "userChangedChannel";

    @Input(INPUT)
    SubscribableChannel userChangedChannel();
}
