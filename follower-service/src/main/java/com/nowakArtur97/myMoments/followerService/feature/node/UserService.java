package com.nowakArtur97.myMoments.followerService.feature.node;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.Record;
import org.neo4j.driver.Value;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.TypeSystem;
import org.springframework.data.neo4j.core.ReactiveNeo4jClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Iterator;

import static com.nowakArtur97.myMoments.followerService.feature.node.Queries.RECOMMEND;

@Service
@RequiredArgsConstructor
@Slf4j
class UserService {

    private final static String MIN_DEGREE_VARIABLE = "$minDegree";
    private final static String MAX_DEGREE_VARIABLE = "$maxDegree";

    private final UserRepository userRepository;
    private final ReactiveNeo4jClient reactiveClient;

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

    Flux<UserNode> recommendUsers(String username, Integer minDegree, Integer maxDegree) {

        log.info("Looking up Users to recommend for User: {} with degree from: {} to: {}", username, minDegree, maxDegree);

        String query = RECOMMEND.replace(MIN_DEGREE_VARIABLE, String.valueOf(minDegree));
        query = query.replace(MAX_DEGREE_VARIABLE, String.valueOf(maxDegree));

        return reactiveClient
                .query(query)
                .bind(username).to("username")
                .fetchAs(UserNode.class)
                .mappedBy(this::extractUserFromRecord)
                .all();
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

    private UserNode extractUserFromRecord(TypeSystem ts, Record record) {
        int nodeIndexInRecord = 0;
        Node userNode = record.values().get(nodeIndexInRecord).asNode();
        Iterator<String> keysIterator = userNode.keys().iterator();
        int usernameIndex;
        int index = 0;
        while (keysIterator.hasNext()) {
            if ("username".equals(keysIterator.next())) {
                break;
            }
            index++;
        }
        usernameIndex = index;
        index = 0;
        String username = null;
        for (Value value : userNode.values()) {
            if (usernameIndex == index) {
                username = value.asString();
            }
            index++;
        }
        return new UserNode(username, Collections.emptySet(), Collections.emptySet());
    }
}
