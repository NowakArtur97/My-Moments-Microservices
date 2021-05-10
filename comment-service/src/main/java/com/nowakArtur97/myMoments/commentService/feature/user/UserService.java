package com.nowakArtur97.myMoments.commentService.feature.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Mono<UserDocument> findByUsername(String username) {

        return userRepository.findByUsername(username);
    }

    public Mono<UserDocument> findByUsernameOrEmail(String usernameOrEmail) {

        return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
    }
}