package com.nowakArtur97.myMoments.followerService.feature.node;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class FollowerService {

    private final UserService userService;

    public Mono<Void> followUser(String username, String usernameToFollow) {

        log.info("Following a User with name: {} by User: {}", usernameToFollow, username);

        return userService.findUserByUsername(username)
                .switchIfEmpty(userService.createUser(username))
                .zipWith(userService.findUserByUsername(usernameToFollow)
                        .switchIfEmpty(userService.createUser((usernameToFollow))))
                .flatMap((tuple) -> {

                    UserNode follower = tuple.getT1();
                    UserNode following = tuple.getT2();

                    boolean isAlreadyFollowing = follower.getFollowing().stream()
                            .anyMatch(f -> f.getFollowerNode().equals(following));

                    if (isAlreadyFollowing) {

                        log.info("User with name: {} is already following: {}", usernameToFollow, username);

                        return Mono.empty();

                    } else {

                        follower.follow(following);

                        log.info("Successfully followed a User with name: {} by User: {}", usernameToFollow, username);

                        return userService.saveUser(follower)
                                .flatMap(user -> Mono.empty());
                    }
                });
    }
}