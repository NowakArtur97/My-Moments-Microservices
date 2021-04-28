package com.nowakArtur97.myMoments.postService.feature.post.resource;

import com.nowakArtur97.myMoments.postService.feature.post.document.PostDocument;
import com.nowakArtur97.myMoments.postService.feature.post.document.PostRepository;
import com.nowakArtur97.myMoments.postService.feature.user.document.UserDocument;
import com.nowakArtur97.myMoments.postService.feature.user.document.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
class PostController {

    private final UserService userService;

    private final PostRepository postRepository;

    @GetMapping("/user")
    @PreAuthorize("hasAuthority('USER_ROLE')")
    Mono<UserDocument> getFlux() {

        return userService.findByUsernameOrEmail("user");
    }

    @GetMapping("/posts")
    @PreAuthorize("hasAuthority('USER_ROLE')")
    Flux<PostDocument> getFlux2() {

        return postRepository.findByUsername("user");
    }
}
