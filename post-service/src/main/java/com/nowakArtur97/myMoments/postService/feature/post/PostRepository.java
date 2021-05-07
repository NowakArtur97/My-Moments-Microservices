package com.nowakArtur97.myMoments.postService.feature.post;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

interface PostRepository extends ReactiveMongoRepository<PostDocument, String> {

    Flux<PostDocument> findByAuthor(String author);
}
