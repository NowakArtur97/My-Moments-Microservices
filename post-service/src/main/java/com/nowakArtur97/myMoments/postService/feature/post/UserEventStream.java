package com.nowakArtur97.myMoments.postService.feature.post;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface UserEventStream {

    String UPDATE_INPUT = "userUpdateChannel";

    @Input(UPDATE_INPUT)
    SubscribableChannel userUpdateChannel();

    String DELETE_INPUT = "userDeleteChannel";

    @Input(DELETE_INPUT)
    SubscribableChannel userDeleteChannel();
}
