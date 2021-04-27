package com.nowakArtur97.myMoments.postService.feature.user.document;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveMongoRepository<UserDocument, String> {

    Mono<UserDocument> findByUsernameOrEmail(String usernameOrEmail);
}
