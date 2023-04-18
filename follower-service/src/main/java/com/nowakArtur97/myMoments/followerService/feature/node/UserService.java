package com.nowakArtur97.myMoments.followerService.feature.node;

import com.nowakArtur97.myMoments.followerService.feature.resource.UserModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.Record;
import org.springframework.data.neo4j.core.ReactiveNeo4jClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.nowakArtur97.myMoments.followerService.feature.node.Queries.*;

@Service
@RequiredArgsConstructor
@Slf4j
class UserService {

    private final static String USERNAME_VARIABLE = "username";
    private final static String FOLLOWING_VARIABLE_NAME = "following";
    private final static String FOLLOWERS_VARIABLE_NAME = "followers";
    private final static String MIN_DEGREE_VARIABLE = "$minDegree";
    private final static String MAX_DEGREE_VARIABLE = "$maxDegree";

    private final UserRepository userRepository;
    private final ReactiveNeo4jClient reactiveClient;

    Mono<UserNode> findUserByUsername(String username) {

        log.info("Looking up a User by username: {}", username);

        return userRepository.findByUsername(username);
    }

    Flux<UserModel> findFollowers(String username) {

        log.info("Looking up Followers of a User: {}", username);

        return extractUserFromRecord(username, FIND_FOLLOWERS);
    }

    Flux<UserModel> findFollowed(String username) {

        log.info("Looking up Followed of a User: {}", username);

        return extractUserFromRecord(username, FIND_FOLLOWED);
    }

    Flux<UserModel> recommendUsers(String username, Integer minDegree, Integer maxDegree) {

        log.info("Looking up Users to recommend for User: {} with degree from: {} to: {}", username, minDegree, maxDegree);

        String query = RECOMMEND.replace(MIN_DEGREE_VARIABLE, String.valueOf(minDegree))
                .replace(MAX_DEGREE_VARIABLE, String.valueOf(maxDegree));

        return extractUserFromRecord(username, query);
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

    private Flux<UserModel> extractUserFromRecord(String username, String query) {
        return reactiveClient.query(query)
                .bind(username).to(USERNAME_VARIABLE)
                .fetchAs(UserModel.class)
                .mappedBy((ts, record) -> mapToUserModel(record))
                .all();
    }

    private UserModel mapToUserModel(Record record) {
        return new UserModel(
                record.get(USERNAME_VARIABLE).asString(),
                record.get(FOLLOWING_VARIABLE_NAME).asInt(),
                record.get(FOLLOWERS_VARIABLE_NAME).asInt());
    }
}
