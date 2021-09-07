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

    // TODO: FollowerService: TEST
    public Mono<Void> followUser(String username, String usernameToFollow) {

        log.info("Following a User with name: {} by user: {}", usernameToFollow, username);

        return userService.findByUsername(username)
                .switchIfEmpty(Mono.just(new UserNode(username)))
                .zipWith(userService.findByUsername(usernameToFollow)
                        .switchIfEmpty(Mono.just((new UserNode(usernameToFollow)))))
                .flatMap((tuple) -> {
                    UserNode follower = tuple.getT1();
                    UserNode following = tuple.getT2();

                    follower.getFollowing().add(new FollowingRelationship(following));

                    userService.save(follower);

                    log.info("Successfully followed a User with name: {} by user: {}", usernameToFollow, username);

                    return Mono.empty();
                });
    }
}