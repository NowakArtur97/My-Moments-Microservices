package com.nowakArtur97.myMoments.postService.feature.post;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class UserEventConsumer {

    private final PostRepository postRepository;

    @StreamListener(UserEventStream.UPDATE_INPUT)
    public void onUserUpdateMessage(Message<UserUpdateEventPayload> message) {

        UserUpdateEventPayload payload = message.getPayload();
        String newUsername = payload.getNewUsername();

        postRepository.findByAuthor(payload.getPreviousUsername())
                .doOnNext(postDocument -> postDocument.setAuthor(newUsername))
                .flatMap(postRepository::save)
                .subscribe();
    }

    @StreamListener(UserEventStream.DELETE_INPUT)
    public void onUserDeleteMessage(Message<String> message) {

        String usernamePayload = message.getPayload();

        postRepository.deleteByAuthor(usernamePayload)
                .subscribe();
    }
}