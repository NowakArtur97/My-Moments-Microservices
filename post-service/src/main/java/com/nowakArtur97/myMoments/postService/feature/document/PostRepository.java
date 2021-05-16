package com.nowakArtur97.myMoments.postService.feature.document;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PostRepository extends ReactiveMongoRepository<PostDocument, String> {

    Flux<PostDocument> findByAuthor(String author);

    Mono<Void> deleteByAuthor(String author);
}
