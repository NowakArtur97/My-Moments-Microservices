package com.nowakArtur97.myMoments.commentService.feature.comment;

import com.nowakArtur97.myMoments.commentService.exception.ForbiddenException;
import com.nowakArtur97.myMoments.commentService.exception.ResourceNotFoundException;
import com.nowakArtur97.myMoments.commentService.feature.user.UserDocument;
import com.nowakArtur97.myMoments.commentService.feature.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
class CommentService {

    private final CommentRepository commentRepository;

    private final UserService userService;

    Mono<CommentDocument> addComment(String postId, String username, CommentDTO commentDTO) {

        return commentRepository.save(new CommentDocument(commentDTO.getContent(), username, postId));
    }

    Mono<CommentDocument> updateComment(String commentId, String postId, String username, CommentDTO commentDTO) {

        return userService.findByUsername(username)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User with name: '" + username + "' not found.")))
                .zipWith(commentRepository.findById(commentId))
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Comment", commentId)))
                .flatMap((tuple) -> {
                    CommentDocument commentDocument = tuple.getT2();
                    UserDocument userDocument = tuple.getT1();

                    if (!commentDocument.getRelatedPostId().equals(postId)) {
                        return Mono.error(new ResourceNotFoundException("Comment with commentId: '" + commentId
                                + "' in the post with id: '" + postId + "' not found."));
                    }

                    if (commentDocument.getAuthor().equals(userDocument.getUsername())) {
                        commentDocument.setContent(commentDTO.getContent());

                        return commentRepository.save(commentDocument);
                    } else {
                        return Mono.error(new ForbiddenException("User can only change his own comments."));
                    }
                });
    }

    Mono<Void> deleteComment(String commentId, String postId, String username) {

        return userService.findByUsername(username)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User with name: '" + username + "' not found.")))
                .zipWith(commentRepository.findById(commentId))
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Comment", commentId)))
                .flatMap((tuple) -> {
                    CommentDocument commentDocument = tuple.getT2();
                    UserDocument userDocument = tuple.getT1();

                    if (!commentDocument.getRelatedPostId().equals(postId)) {
                        return Mono.error(new ResourceNotFoundException("Comment with commentId: '" + commentId
                                + "' in the post with id: '" + postId + "' not found."));
                    }

                    if (commentDocument.getAuthor().equals(userDocument.getUsername())) {

                        return commentRepository.delete(commentDocument);
                    } else {
                        return Mono.error(new ForbiddenException("User can only change his own comments."));
                    }
                });
    }
}
