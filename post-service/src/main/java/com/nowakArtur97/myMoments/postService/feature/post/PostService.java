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

    Mono<PostDocument> createPost(@Valid PostDTO postDTO, String username) {

        PostDocument postDocument = new PostDocument(postDTO.getCaption(), username, postDTO.getPhotos());

        return postRepository.save(postDocument);
    }
}
