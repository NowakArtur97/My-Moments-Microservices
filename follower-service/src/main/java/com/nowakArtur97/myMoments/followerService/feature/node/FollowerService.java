package com.nowakArtur97.myMoments.followerService.feature.node;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class FollowerService {

    private final UserService userRepository;

    // TODO: FollowerService: TEST
    public Mono<Void> followUser(String username, String usernameToFollow) {

        log.info("Following a User with name: {} by User: {}", usernameToFollow, username);

        return userRepository.findUserByUsername(username)
//                .switchIfEmpty(Mono.just(new UserNode(username)))
                .switchIfEmpty(userRepository.createUser(username))
                .zipWith(userRepository.findUserByUsername(usernameToFollow)
//                        .switchIfEmpty(Mono.just(new UserNode(usernameToFollow))))
                        .switchIfEmpty(userRepository.createUser((usernameToFollow))))
                .flatMap((tuple) -> {

                    UserNode follower = tuple.getT1();
                    UserNode following = tuple.getT2();

                    follower.getFollowing().add(new FollowingRelationship(following));
                    following.getFollowers().add(new FollowingRelationship(follower));

                    log.info("Successfully followed a User with name: {} by User: {}", usernameToFollow, username);

                    return userRepository.saveUser(follower)
                            .flatMap(user -> Mono.empty());
                });
    }
}