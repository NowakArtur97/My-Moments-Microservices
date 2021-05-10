package com.nowakArtur97.myMoments.commentService.feature.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
class CommentService {

    private final CommentRepository commentRepository;

    Mono<CommentDocument> addComment(String postId, String username, CommentDTO commentDTO) {

        return commentRepository.save(new CommentDocument(commentDTO.getContent(), username, postId));
    }
}
