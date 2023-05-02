package com.nowakArtur97.myMoments.postService.feature.document;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface PostRepository extends ReactiveMongoRepository<PostDocument, String> {

    Flux<PostDocument> findByAuthor(String author);

    Flux<PostDocument> findByAuthorIn(List<String> authors, Pageable page);

    Mono<Void> deleteByAuthor(String author);
}
