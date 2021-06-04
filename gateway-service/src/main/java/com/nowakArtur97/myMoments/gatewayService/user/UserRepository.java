package com.nowakArtur97.myMoments.gatewayService.user;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

interface UserRepository extends ReactiveMongoRepository<UserDocument, String> {

    Mono<UserDocument> findByUsername(String username);
}