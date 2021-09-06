package com.nowakArtur97.myMoments.followerService.feature.node;

import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import reactor.core.publisher.Mono;

interface UserRepository extends ReactiveNeo4jRepository<UserNode, Long> {

    Mono<UserNode> findByUsername(String username);
}
