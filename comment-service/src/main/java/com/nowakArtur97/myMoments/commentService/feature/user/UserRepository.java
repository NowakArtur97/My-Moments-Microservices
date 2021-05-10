package com.nowakArtur97.myMoments.commentService.feature.user;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

interface UserRepository extends ReactiveMongoRepository<UserDocument, String> {

    Mono<UserDocument> findByUsername(String username);

    Mono<UserDocument> findByUsernameOrEmail(String username, String email);
}