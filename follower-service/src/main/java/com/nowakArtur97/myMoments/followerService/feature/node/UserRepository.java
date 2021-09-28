package com.nowakArtur97.myMoments.followerService.feature.node;

import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

interface UserRepository extends ReactiveNeo4jRepository<UserNode, Long> {

    Mono<UserNode> findByUsername(String username);

    @Query(Queries.FIND_FOLLOWERS)
    Flux<UserNode> findFollowers(String username);
}
