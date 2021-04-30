package com.nowakArtur97.myMoments.postService.feature.post;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

interface PostRepository extends ReactiveMongoRepository<PostDocument, String> {
}
