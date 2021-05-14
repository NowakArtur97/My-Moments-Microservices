package com.nowakArtur97.myMoments.postService.feature.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class UserEventConsumer {

    private final PostRepository postRepository;

    @StreamListener(UserEventStream.UPDATE_INPUT)
    public void onUserUpdateMessage(Message<UserUpdateEventPayload> message) {

        UserUpdateEventPayload payload = message.getPayload();
        String newUsername = payload.getNewUsername();

        log.info(newUsername);

        postRepository.findByAuthor(payload.getPreviousUsername())
                .doOnNext(postDocument -> postDocument.setAuthor(newUsername))
                .flatMap(postRepository::save)
                .subscribe();
    }

    @StreamListener(UserEventStream.DELETE_INPUT)
    public void onUserDeleteMessage(Message<String> message) {

        String usernamePayload = message.getPayload();

        log.info(usernamePayload);

        postRepository.deleteByAuthor(usernamePayload)
                .subscribe();
    }
}