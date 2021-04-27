package com.nowakArtur97.myMoments.postService.feature.post.document;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface PostRepository extends ReactiveMongoRepository<PostDocument, String> {

    Flux<PostDocument> findByUsername(String username);
}
