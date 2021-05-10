package com.nowakArtur97.myMoments.commentService.feature.comment;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

interface CommentRepository extends ReactiveMongoRepository<CommentDocument, String> {
}
