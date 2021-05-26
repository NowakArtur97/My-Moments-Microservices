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
class UserEventConsumer {

    private final CommentRepository commentRepository;

    @StreamListener(UserEventStream.UPDATE_INPUT)
    public void onUserUpdateMessage(Message<UserUpdateEventPayload> message) {

        UserUpdateEventPayload payload = message.getPayload();
        String newUsername = payload.getNewUsername();

        log.info("Consuming User Update Event for user: {}", newUsername);

        commentRepository.findByAuthor(payload.getPreviousUsername())
                .doOnNext(postDocument -> postDocument.setAuthor(newUsername))
                .flatMap(commentRepository::save)
                .subscribe();
    }

    @StreamListener(UserEventStream.DELETE_INPUT)
    public void onUserDeleteMessage(Message<String> message) {

        String usernamePayload = message.getPayload();

        log.info("Consuming User Delete Event for user: {}", usernamePayload);

        commentRepository.deleteByAuthor(usernamePayload)
                .subscribe();
    }
}
