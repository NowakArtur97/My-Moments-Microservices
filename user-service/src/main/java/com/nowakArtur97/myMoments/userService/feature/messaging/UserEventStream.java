package com.nowakArtur97.myMoments.userService.feature.messaging;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface UserEventStream {

    String OUTPUT = "userChangedChannel";

    @Output(OUTPUT)
    MessageChannel userChangedChannel();
}
