package com.nowakArtur97.myMoments.postService.feature.document;

import com.nowakArtur97.myMoments.postService.exception.ForbiddenException;
import com.nowakArtur97.myMoments.postService.exception.ResourceNotFoundException;
import com.nowakArtur97.myMoments.postService.feature.messaging.PostEventProducer;
import com.nowakArtur97.myMoments.postService.feature.resource.PostDTO;
import com.nowakArtur97.myMoments.postService.feature.resource.PostsCommentsModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Service
@Validated
public class PostService {

    private final PostRepository postRepository;

    private final PostEventProducer postEventProducer;

    private final WebClient.Builder webClientBuilder;

    private final String commentServiceUri;

    @Autowired
    public PostService(PostRepository postRepository, PostEventProducer postEventProducer, WebClient.Builder webClientBuilder,
                       @Value("${my-moments.comment-service-uri:lb://comment-service/api/v1/posts/{postId}/comments}")
                               String commentServiceUri) {
        this.postRepository = postRepository;
        this.postEventProducer = postEventProducer;
        this.webClientBuilder = webClientBuilder;
        this.commentServiceUri = commentServiceUri;
    }

    public Mono<PostDocument> findPostById(String id) {

        return postRepository.findById(id);
    }

    public Flux<PostDocument> findPostsByAuthor(String author) {

        return postRepository.findByAuthor(author);
    }

    public Mono<PostsCommentsModel> getCommentsByPostId(String id) {

        return webClientBuilder.build().get()
                .uri(commentServiceUri, id)
                .retrieve()
                .bodyToMono(PostsCommentsModel.class);
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
