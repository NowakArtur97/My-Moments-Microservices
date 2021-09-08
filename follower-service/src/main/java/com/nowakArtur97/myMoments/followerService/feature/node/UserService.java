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

    Mono<UserNode> findUserByUsername(String username) {

        return userRepository.findByUsername(username);
    }

    Mono<UserNode> createUser(String username) {

        log.info("Creating a new User: {}", username);

        Mono<UserNode> userNodeMono = userRepository.save(new UserNode(username));

        log.info("Successfully created a User: {}", username);

        return userNodeMono;
    }

    Mono<UserNode> saveUser(UserNode userNode) {

        log.info("Saving a User: {}", userNode.getUsername());

        Mono<UserNode> userNodeMono = userRepository.save(userNode);

        log.info("Successfully saved a User: {}", userNode.getUsername());

        return userNodeMono;
    }
}
