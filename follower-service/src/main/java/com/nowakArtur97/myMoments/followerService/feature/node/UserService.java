package com.nowakArtur97.myMoments.followerService.feature.node;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
class UserService {

    private final UserRepository userRepository;

    // TODO: UserService: TEST
    Mono<UserNode> findUserByUsername(String username) {

        return userRepository.findByUsername(username);
    }

    // TODO: UserService: TEST
    Mono<UserNode> createUser(String username) {

        log.info("Creating a new User: {}", username);

        Mono<UserNode> userNodeMono = userRepository.save(new UserNode(username));

        log.info("Successfully created a User: {}", username);

        return userNodeMono;
    }

    // TODO: UserService: TEST
    Mono<UserNode> saveUser(UserNode userNode) {

        log.info("Saving a User: {}", userNode.getUsername());

        Mono<UserNode> userNodeMono = userRepository.save(userNode);

        log.info("Successfully saved a User: {}", userNode.getUsername());

        return userNodeMono;
    }
//
//    public Flux<UserNode> saveAll(List<UserNode> users) {
//
//        log.info("Creating new Users: {}", users.stream().map(UserNode::getUsername).collect(Collectors.toList()));
//
//        Flux<UserNode> usersNodesFlux = userRepository.saveAll(users);
//
//        users.forEach(userNode -> log.info("Successfully created a User: {}", userNode.getUsername()));
//
//        return usersNodesFlux;
//    }
}
