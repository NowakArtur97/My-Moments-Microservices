package com.nowakArtur97.myMoments.postService.feature.post;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface PostEventStream {

    String DELETE_OUTPUT = "postDeleteChannel";

    @Output(DELETE_OUTPUT)
    MessageChannel postDeleteChannel();
}
