package com.nowakArtur97.myMoments.postService.feature.post.resource;

import com.nowakArtur97.myMoments.postService.feature.post.document.PostDocument;
import com.nowakArtur97.myMoments.postService.feature.post.document.PostRepository;
import com.nowakArtur97.myMoments.postService.feature.user.document.UserDocument;
import com.nowakArtur97.myMoments.postService.feature.user.document.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/posts")
class PostController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @GetMapping("/user")
    @PreAuthorize("hasAuthority('USER_ROLE')")
    Mono<UserDocument> getFlux() {

        return userRepository.findByUsernameOrEmail("user", "user");
    }

    @GetMapping("/posts")
    @PreAuthorize("hasAuthority('USER_ROLE')")
    Flux<PostDocument> getFlux2() {

        return postRepository.findByUsername("user");
    }
}
