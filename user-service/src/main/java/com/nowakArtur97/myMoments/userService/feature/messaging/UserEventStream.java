package com.nowakArtur97.myMoments.userService.feature.messaging;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface UserEventStream {

    String UPDATE_OUTPUT = "userUpdateChannel";

    @Output(UPDATE_OUTPUT)
    MessageChannel userUpdateChannel();

    String DELETE_OUTPUT = "userDeleteChannel";

    @Output(DELETE_OUTPUT)
    MessageChannel userDeleteChannel();
}
