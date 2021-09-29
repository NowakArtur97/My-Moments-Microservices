package com.nowakArtur97.myMoments.followerService.feature.node;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
class UserService {

    private final UserRepository userRepository;

    Mono<UserNode> findUserByUsername(String username) {

        log.info("Looking up a User by username: {}", username);

        return userRepository.findByUsername(username);
    }

    Flux<UserNode> findFollowers(String username) {

        log.info("Looking up Followers of a User: {}", username);

        return userRepository.findFollowers(username);
    }

    Flux<UserNode> findFollowed(String username) {

        log.info("Looking up Followed of a User: {}", username);

        return userRepository.findFollowed(username);
    }

    Mono<UserNode> saveUser(UserNode userNode) {

        log.info("Saving a User: {}", userNode.getUsername());

        Mono<UserNode> userNodeMono = userRepository.save(userNode);

        log.info("Successfully saved a User: {}", userNode.getUsername());

        return userNodeMono;
    }

    Mono<Void> followUser(String username, String usernameToFollow) {

        log.info("Following a User with name: {} by User: {}", usernameToFollow, username);

        Mono<Void> followVoid = userRepository.follow(username, usernameToFollow);

        log.info("Successfully followed a User with name: {} by User: {}", usernameToFollow, username);

        return followVoid;
    }

    Mono<Void> unfollowUser(String username, String usernameToFollow) {

        log.info("Unfollowing a User with name: {} by User: {}", usernameToFollow, username);

        Mono<Void> unfollowVoid = userRepository.unfollow(username, usernameToFollow);

        log.info("Successfully unfollowed a User with name: {} by User: {}", usernameToFollow, username);

        return unfollowVoid;
    }
}
