package com.nowakArtur97.myMoments.commentService.feature.document;

import com.nowakArtur97.myMoments.commentService.exception.ForbiddenException;
import com.nowakArtur97.myMoments.commentService.exception.ResourceNotFoundException;
import com.nowakArtur97.myMoments.commentService.feature.resource.CommentDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public Flux<CommentDocument> findCommentsByRelatedPostId(String relatedPostId) {

        return commentRepository.findByRelatedPost(relatedPostId);
    }

    public Mono<CommentDocument> addComment(String postId, String username, CommentDTO commentDTO) {

        return commentRepository.save(new CommentDocument(commentDTO.getContent(), username, postId));
    }

    public Mono<CommentDocument> updateComment(String commentId, String postId, String username, CommentDTO commentDTO) {

        return commentRepository.findById(commentId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Comment", commentId)))
                .flatMap((commentDocument) -> {

                    if (!commentDocument.getRelatedPost().equals(postId)) {
                        return Mono.error(new ResourceNotFoundException("Comment with commentId: '" + commentId
                                + "' in the post with id: '" + postId + "' not found."));
                    }

                    if (username.equals(commentDocument.getAuthor())) {
                        commentDocument.setContent(commentDTO.getContent());

                        return commentRepository.save(commentDocument);
                    } else {
                        return Mono.error(new ForbiddenException("User can only change his own comments."));
                    }
                });
    }

    public Mono<Void> deleteComment(String commentId, String postId, String username) {

        return commentRepository.findById(commentId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Comment", commentId)))
                .flatMap((commentDocument) -> {

                    if (!commentDocument.getRelatedPost().equals(postId)) {
                        return Mono.error(new ResourceNotFoundException("Comment with commentId: '" + commentId
                                + "' in the post with id: '" + postId + "' not found."));
                    }

                    if (username.equals(commentDocument.getAuthor())) {

                        return commentRepository.delete(commentDocument);
                    } else {
                        return Mono.error(new ForbiddenException("User can only change his own comments."));
                    }
                });
    }
}
