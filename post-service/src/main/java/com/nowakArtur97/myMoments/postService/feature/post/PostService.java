package com.nowakArtur97.myMoments.postService.feature.post;

import com.nowakArtur97.myMoments.postService.exception.ForbiddenException;
import com.nowakArtur97.myMoments.postService.exception.ResourceNotFoundException;
import com.nowakArtur97.myMoments.postService.feature.user.UserDocument;
import com.nowakArtur97.myMoments.postService.feature.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Service
@RequiredArgsConstructor
@Validated
class PostService {

    private final PostRepository postRepository;

    private final UserService userService;

    Mono<PostDocument> findPostById(String id) {

        return postRepository.findById(id);
    }

    Mono<PostDocument> createPost(String username, @Valid PostDTO postDTO) {

        return postRepository.save(new PostDocument(postDTO.getCaption(), username, postDTO.getPhotos()));
    }

    Mono<PostDocument> updatePost(String postId, String username, @Valid PostDTO postDTO) {

        return userService.findByUsername(username)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User with name: '" + username + "' not found.")))
                .zipWith(postRepository.findById(postId))
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Post", postId)))
                .flatMap((tuple) -> {
                    PostDocument postDocument = tuple.getT2();
                    UserDocument userDocument = tuple.getT1();

                    if (postDocument.getAuthor().equals(userDocument.getUsername())) {
                        postDocument.setId(postId);
                        postDocument.setCaption(postDTO.getCaption());
                        postDocument.setPhotos(postDTO.getPhotos());

                        return postRepository.save(postDocument);
                    } else {
                        return Mono.error(new ForbiddenException("User can only change his own posts."));
                    }
                });
    }

    Mono<Void> deletePost(String postId, String username) {

        return userService.findByUsername(username)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User with name: '" + username + "' not found.")))
                .zipWith(postRepository.findById(postId))
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Post", postId)))
                .flatMap((tuple) -> {
                    PostDocument postDocument = tuple.getT2();
                    UserDocument userDocument = tuple.getT1();

                    if (postDocument.getAuthor().equals(userDocument.getUsername())) {

                        return postRepository.delete(postDocument);
                    } else {
                        return Mono.error(new ForbiddenException("User can only change his own posts."));
                    }
                });
    }
}
