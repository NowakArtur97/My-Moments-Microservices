package com.nowakArtur97.myMoments.commentService.feature.document;

import com.nowakArtur97.myMoments.commentService.exception.ForbiddenException;
import com.nowakArtur97.myMoments.commentService.exception.ResourceNotFoundException;
import com.nowakArtur97.myMoments.commentService.feature.resource.CommentDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;

    public Flux<CommentDocument> findCommentsByRelatedPost(String postId) {

        log.info("Looking up Comments with Post id: {}", postId);

        return commentRepository.findByRelatedPost(postId);
    }

    public Mono<CommentDocument> addComment(String postId, String username, CommentDTO commentDTO) {

        log.info("Creating a new Comment for user: {} and Post with id: {}", username, postId);

        Mono<CommentDocument> commentDocumentMono
                = commentRepository.save(new CommentDocument(commentDTO.getContent(), username, postId));

        log.info("Successfully created a new Comment for user: {} and Post with id: {}", username, postId);

        return commentDocumentMono;
    }

    public Mono<CommentDocument> updateComment(String commentId, String postId, String username, CommentDTO commentDTO) {

        log.info("Updating a Comment with id: {} by user: {} for Post with id: {}", commentId, username, postId);

        return commentRepository.findById(commentId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Comment", commentId)))
                .flatMap((commentDocument) -> {

                    if (!commentDocument.getRelatedPost().equals(postId)) {
                        return Mono.error(new ResourceNotFoundException("Comment with commentId: '" + commentId
                                + "' in the post with id: '" + postId + "' not found."));
                    }

                    if (username.equals(commentDocument.getAuthor())) {
                        commentDocument.setContent(commentDTO.getContent());

                        Mono<CommentDocument> commentDocumentMono = commentRepository.save(commentDocument);

                        log.info("Successfully updated a Comment with id: {} by user: {} for Post with id: {}",
                                commentId, username, postId);

                        return commentDocumentMono;
                    } else {

                        log.info("User: {} tried to update someone else's Comment with id: {} for Post with id: {}",
                                username, commentId, postId);

                        return Mono.error(new ForbiddenException("User can only change his own comments."));
                    }
                });
    }

    public Mono<Void> deleteComment(String commentId, String postId, String username) {

        log.info("Deleting a Comment with id: {} by user: {} for Post with id: {}", commentId, username, postId);

        return commentRepository.findById(commentId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Comment", commentId)))
                .flatMap((commentDocument) -> {

                    if (!commentDocument.getRelatedPost().equals(postId)) {
                        return Mono.error(new ResourceNotFoundException("Comment with commentId: '" + commentId
                                + "' in the post with id: '" + postId + "' not found."));
                    }

                    if (username.equals(commentDocument.getAuthor())) {

                        Mono<Void> commentDocumentMono = commentRepository.delete(commentDocument);

                        log.info("Successfully deleted a Comment with id: {} by user: {} for Post with id: {}",
                                commentId, username, postId);

                        return commentDocumentMono;
                    } else {

                        log.info("User: {} tried to delete someone else's Comment with id: {} for Post with id: {}",
                                username, commentId, postId);

                        return Mono.error(new ForbiddenException("User can only change his own comments."));
                    }
                });
    }
}
