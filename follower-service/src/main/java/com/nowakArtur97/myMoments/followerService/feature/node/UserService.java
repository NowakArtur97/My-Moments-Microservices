package com.nowakArtur97.myMoments.followerService.feature.node;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
class UserService {

    private final UserRepository userRepository;

    // TODO: TEST
    Mono<UserNode> findByUsername(String username) {

        log.info("Looking up a User by name: {}", username);

        return userRepository.findByUsername(username);
    }

    // TODO: TEST
    Mono<UserNode> save(UserNode userNode) {

        Mono<UserNode> userNodeMono = userRepository.save(userNode);

        log.info("Successfully created a User with name: {}", userNode.getUsername());

        return userNodeMono;
    }
}
