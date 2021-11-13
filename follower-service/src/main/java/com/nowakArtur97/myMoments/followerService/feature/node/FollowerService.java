package com.nowakArtur97.myMoments.followerService.feature.node;

import com.nowakArtur97.myMoments.followerService.exception.ForbiddenException;
import com.nowakArtur97.myMoments.followerService.exception.ResourceNotFoundException;
import com.nowakArtur97.myMoments.followerService.feature.resource.UserModel;
import com.nowakArtur97.myMoments.followerService.feature.resource.UsersAcquaintancesModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FollowerService {

    private final UserService userService;

    public Mono<Void> followUser(String username, String usernameToFollow) {

        if (username.equals(usernameToFollow)) {
            return Mono.error(
                    new ForbiddenException("User with username: '" + username + "' cannot follow himself."));
        }

        return userService.findUserByUsername(username)
                .switchIfEmpty(Mono.defer(() -> userService.saveUser(new UserNode(username))))
                .zipWith(userService.findUserByUsername(usernameToFollow)
                        .switchIfEmpty(Mono.defer(() -> userService.saveUser(new UserNode(usernameToFollow)))))
                .flatMap((tuple) -> {

                    UserNode follower = tuple.getT1();
                    UserNode following = tuple.getT2();

                    boolean isAlreadyFollowing = follower.getFollowing().stream()
                            .anyMatch(f -> f.getFollowerNode().equals(following));

                    if (isAlreadyFollowing) {

                        return Mono.error(
                                new ForbiddenException("User with username: '" + username + "' is already following: "
                                        + usernameToFollow + "."));

                    } else {

                        return userService.followUser(username, usernameToFollow)
                                .flatMap(__ -> Mono.empty());
                    }
                });
    }

    public Mono<Void> unfollowUser(String username, String usernameToUnfollow) {

        if (username.equals(usernameToUnfollow)) {
            return Mono.error(
                    new ForbiddenException("User with username: '" + username + "' cannot unfollow himself."));
        }

        return userService.findUserByUsername(username)
                .switchIfEmpty(Mono.defer(() ->
                        Mono.error(new ResourceNotFoundException("User with username: '" + username + "' not found."))))
                .zipWith(userService.findUserByUsername(usernameToUnfollow)
                        .switchIfEmpty(Mono.defer(() ->
                                Mono.error(new ResourceNotFoundException("Follower with username: '" + usernameToUnfollow + "' not found.")))))
                .flatMap((tuple) -> {

                    UserNode follower = tuple.getT1();
                    UserNode following = tuple.getT2();

                    boolean isFollowing = follower.getFollowing().stream()
                            .anyMatch(f -> f.getFollowerNode().equals(following));

                    if (isFollowing) {

                        return userService.unfollowUser(username, usernameToUnfollow)
                                .flatMap(__ -> Mono.empty());

                    } else {

                        return Mono.error(
                                new ForbiddenException("User with name: '" + username + "' is not following: '"
                                        + usernameToUnfollow + "'."));
                    }
                });
    }

    public Mono<UsersAcquaintancesModel> findFollowers(String username) {

        return mapFluxToUsersAcquaintancesModel(userService.findFollowers(username), username);
    }

    public Mono<UsersAcquaintancesModel> findFollowed(String username) {

        return mapFluxToUsersAcquaintancesModel(userService.findFollowed(username), username);
    }


    public Mono<UsersAcquaintancesModel> recommendUsers(String username, Integer minDegree, Integer maxDegree) {

        return mapFluxToUsersAcquaintancesModel(userService.recommendUsers(username, minDegree, maxDegree), username);
    }

    private Mono<UsersAcquaintancesModel> mapFluxToUsersAcquaintancesModel(Flux<UserNode> users, String username) {

        return users
                .map(follower -> new UserModel(follower.getUsername()))
                .collect(Collectors.toList())
                .map(UsersAcquaintancesModel::new);
    }
}
