package com.nowakArtur97.myMoments.postService.feature.post;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Service
@RequiredArgsConstructor
@Validated
class PostService {

    private final PostRepository postRepository;

    Mono<PostDocument> findPostById(String id) {

        return postRepository.findById(id);
    }

    Mono<PostDocument> createPost(@Valid PostDTO postDTO, String username) {

        return postRepository.save(new PostDocument(postDTO.getCaption(), username, postDTO.getPhotos()));
    }
}
