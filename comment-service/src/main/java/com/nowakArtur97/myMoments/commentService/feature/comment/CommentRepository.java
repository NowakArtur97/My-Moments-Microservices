package com.nowakArtur97.myMoments.commentService.feature.comment;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

interface CommentRepository extends ReactiveMongoRepository<CommentDocument, String> {

    Flux<CommentDocument> findByAuthor(String author);

    Mono<Void> deleteByAuthor(String author);
}
