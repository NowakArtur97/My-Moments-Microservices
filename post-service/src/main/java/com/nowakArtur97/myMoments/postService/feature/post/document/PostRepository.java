package com.nowakArtur97.myMoments.postService.feature.post.document;

import com.nowakArtur97.myMoments.postService.feature.user.document.UserDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

interface PostRepository extends ReactiveMongoRepository<PostDocument, String> {

    Mono<PostDocument> findByUsername(String username);
}
