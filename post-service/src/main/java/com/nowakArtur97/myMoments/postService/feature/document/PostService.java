package com.nowakArtur97.myMoments.postService.feature.document;

import com.nowakArtur97.myMoments.postService.exception.ForbiddenException;
import com.nowakArtur97.myMoments.postService.exception.ResourceNotFoundException;
import com.nowakArtur97.myMoments.postService.feature.messaging.PostEventProducer;
import com.nowakArtur97.myMoments.postService.feature.resource.PostDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Service
@RequiredArgsConstructor
@Validated
public class PostService {

    private final PostRepository postRepository;

    private final PostEventProducer postEventProducer;

    public Mono<PostDocument> findPostById(String id) {

        return postRepository.findById(id);
    }

    public Flux<PostDocument> findPostsByAuthor(String author) {

        return postRepository.findByAuthor(author);
    }

    public Mono<PostDocument> createPost(String username, @Valid PostDTO postDTO) {

        return postRepository.save(new PostDocument(postDTO.getCaption(), username, postDTO.getPhotos()));
    }

    public Mono<PostDocument> updatePost(String postId, String username, @Valid PostDTO postDTO) {

        return postRepository.findById(postId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Post", postId)))
                .flatMap((postDocument) -> {

                    if (username.equals(postDocument.getAuthor())) {
                        postDocument.setCaption(postDTO.getCaption());
                        postDocument.setPhotos(postDTO.getPhotos());

                        return postRepository.save(postDocument);
                    } else {
                        return Mono.error(new ForbiddenException("User can only change his own posts."));
                    }
                });
    }

    public Mono<Void> deletePost(String postId, String username) {

        return postRepository.findById(postId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Post", postId)))
                .flatMap((postDocument) -> {

                    if (username.equals(postDocument.getAuthor())) {

                        return postRepository.delete(postDocument);
                    } else {
                        return Mono.error(new ForbiddenException("User can only change his own posts."));
                    }
                }).doOnSuccess((__) -> postEventProducer.sendPostDeleteEvent(postId));
    }
}
