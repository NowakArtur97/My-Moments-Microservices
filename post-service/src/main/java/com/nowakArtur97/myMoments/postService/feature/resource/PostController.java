package com.nowakArtur97.myMoments.postService.feature.resource;

import com.nowakArtur97.myMoments.postService.exception.ResourceNotFoundException;
import com.nowakArtur97.myMoments.postService.feature.document.PostService;
import com.nowakArtur97.myMoments.postService.jwt.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.Part;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@Tag(name = PostTag.RESOURCE, description = PostTag.DESCRIPTION)
@ApiResponses(value = {
        @ApiResponse(responseCode = "401", description = "Permission to the resource is prohibited"),
        @ApiResponse(responseCode = "403", description = "Access to the resource is prohibited")})
class PostController {

    private final PostService postService;

    private final JwtUtil jwtUtil;

    private final PostObjectMapper postObjectMapper;

    private final ModelMapper modelMapper;

    @GetMapping(path = "/{id}")
    @Operation(summary = "Get a post", description = "Provide an id",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully found post"),
            @ApiResponse(responseCode = "400", description = "Invalid Post's id supplied")})
    Mono<ResponseEntity<PostModelWithComments>> getPostWithComments(
            @Parameter(description = "Id of the Post being looked up", name = "id", required = true, example = "id")
            @PathVariable("id") String id
    ) {

        return postService.getCommentsByPostId(id)
                .zipWith(postService.findPostById(id))
                .map(tuple -> {
                    PostModelWithComments postModelWithComments = modelMapper.map(tuple.getT2(), PostModelWithComments.class);
                    postModelWithComments.setComments(tuple.getT1().getComments());
                    return postModelWithComments;
                })
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Post", id)));
    }

    @GetMapping(path = "/me")
    @Operation(summary = "Find authenticated User's Posts", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully found posts"),
            @ApiResponse(responseCode = "404", description = "Could not find User with provided token")})
    Mono<ResponseEntity<UsersPostsModel>> getAuthenticatedUsersPosts(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader) {

        return Mono.just(jwtUtil.extractUsernameFromHeader(authorizationHeader))
                .flatMapMany(postService::findPostsByAuthor)
                .map(postDocument -> modelMapper.map(postDocument, PostModel.class))
                .collectList()
                .map(UsersPostsModel::new)
                .map(ResponseEntity::ok);
    }

    @GetMapping
    @Operation(summary = "Find User's Posts by Username", description = "Provide a name to look up specific Posts",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User's posts found"),
            @ApiResponse(responseCode = "400", description = "Invalid User's name supplied")})
    Mono<ResponseEntity<UsersPostsModel>> getUsersPosts(
            @Parameter(description = "Username of the Posts being looked up", name = "username", required = true,
                    example = "user") @RequestParam("username") String username) {

        return postService.findPostsByAuthor(username)
                .map(postDocument -> modelMapper.map(postDocument, PostModel.class))
                .collectList()
                .map(UsersPostsModel::new)
                .map(ResponseEntity::ok);
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Create a post", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Successfully created post"),
            @ApiResponse(responseCode = "400", description = "Incorrectly entered data")})
    Mono<ResponseEntity<PostModel>> cretePost(
            @Parameter(description = "The post's photos", name = "photos", required = true)
            @RequestPart(value = "photos", required = false) Flux<Part> photos,
            // required = false - Not required to bypass the exception with a missing request part
            // and return a validation failed message
            @Parameter(description = "The post's data", name = "post")
            @RequestPart(value = "post", required = false) String post,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader
    ) {

        return Mono.just(jwtUtil.extractUsernameFromHeader(authorizationHeader))
                .zipWith(postObjectMapper.getPostDTOFromString(post, photos))
                .flatMap((tuple) -> postService.createPost(tuple.getT1(), tuple.getT2()))
                .map(postDocument -> modelMapper.map(postDocument, PostModel.class))
                .map(postModel -> ResponseEntity.created(URI.create("/api/v1/posts/" + postModel.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(postModel));
    }

    @PutMapping(path = "/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "Update a post", description = "Provide an id",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully updated post"),
            @ApiResponse(responseCode = "400", description = "Invalid Post's id supplied or incorrectly entered data"),
            @ApiResponse(responseCode = "404", description = "Could not find Post with provided id")})
    Mono<ResponseEntity<PostModel>> updatePost(
            @Parameter(description = "Id of the Post being updated", name = "id", required = true, example = "id")
            @PathVariable("id") String id,
            @Parameter(description = "The post's photos", name = "photos", required = true)
            @RequestPart(value = "photos", required = false) Flux<Part> photos,
            // required = false - Not required to bypass the exception with a missing request part
            // and return a validation failed message
            @Parameter(description = "The post's data", name = "post")
            @RequestPart(value = "post", required = false) String post,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader
    ) {

        return Mono.just(jwtUtil.extractUsernameFromHeader(authorizationHeader))
                .zipWith(postObjectMapper.getPostDTOFromString(post, photos))
                .flatMap((tuple) -> postService.updatePost(id, tuple.getT1(), tuple.getT2()))
                .map(postDocument -> modelMapper.map(postDocument, PostModel.class))
                .map(ResponseEntity::ok);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Added to remove the default 200 status added by Swagger
    @Operation(summary = "Delete a post", description = "Provide an id",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Successfully deleted a post"),
            @ApiResponse(responseCode = "400", description = "Invalid Post's id supplied"),
            @ApiResponse(responseCode = "404", description = "Could not find Post with provided id")})
    Mono<ResponseEntity<Void>> deletePost(
            @Parameter(description = "Id of the Post being deleted", name = "id",
                    required = true, example = "id")
            @PathVariable("id") String id,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader) {

        return Mono.just(jwtUtil.extractUsernameFromHeader(authorizationHeader))
                .flatMap((username) -> postService.deletePost(id, username))
                .map((postDocumentVoid) -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                        .body(postDocumentVoid));
    }
}
