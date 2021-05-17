package com.nowakArtur97.myMoments.commentService.feature.document;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CommentRepository extends ReactiveMongoRepository<CommentDocument, String> {

    Flux<CommentDocument> findByAuthor(String author);

    Mono<Void> deleteByAuthor(String author);

    Mono<Void> deleteByRelatedPostId(String relatedPostId);
}
