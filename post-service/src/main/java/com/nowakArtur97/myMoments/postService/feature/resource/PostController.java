package com.nowakArtur97.myMoments.postService.feature.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nowakArtur97.myMoments.postService.advice.ErrorResponse;
import com.nowakArtur97.myMoments.postService.exception.ResourceNotFoundException;
import com.nowakArtur97.myMoments.postService.feature.document.PostService;
import com.nowakArtur97.myMoments.postService.jwt.JwtUtil;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@Api(tags = {PostTag.RESOURCE})
@ApiResponses(value = {
        @ApiResponse(code = 401, message = "Permission to the resource is prohibited"),
        @ApiResponse(code = 403, message = "Access to the resource is prohibited")})
class PostController {

    private final PostService postService;

    private final JwtUtil jwtUtil;

    private final PostObjectMapper postObjectMapper;

    private final ModelMapper modelMapper;

    @GetMapping(path = "/{id}")
    @ApiOperation(value = "Get a post", notes = "Provide an id")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully found post", response = PostModelWithComments.class),
            @ApiResponse(code = 400, message = "Invalid Post's id supplied", response = ErrorResponse.class)})
    Mono<ResponseEntity<PostModelWithComments>> getPostWithComments(
            @ApiParam(value = "Id of the Post being looked up", name = "id", type = "string",
                    required = true, example = "id") @PathVariable("id") String id
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
    @ApiOperation(value = "Find authenticated User's Posts")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully found posts", response = UsersPostsModel.class),
            @ApiResponse(code = 404, message = "Could not find User with provided token", response = ErrorResponse.class)})
    Mono<ResponseEntity<UsersPostsModel>> getAuthenticatedUsersPosts(
            @ApiParam(hidden = true) @RequestHeader("Authorization") String authorizationHeader) {

        return Mono.just(jwtUtil.extractUsernameFromHeader(authorizationHeader))
                .flatMapMany(postService::findPostsByAuthor)
                .map(postDocument -> modelMapper.map(postDocument, PostModel.class))
                .collectList()
                .map(UsersPostsModel::new)
                .map(ResponseEntity::ok);
    }

    @GetMapping
    @ApiOperation(value = "Find User's Posts by Username", notes = "Provide a name to look up specific Posts")
    @ApiResponses({
            @ApiResponse(code = 200, message = "User's posts found", response = UsersPostsModel.class),
            @ApiResponse(code = 400, message = "Invalid User's name supplied", response = ErrorResponse.class)})
    Mono<ResponseEntity<UsersPostsModel>> getUsersPosts(
            @ApiParam(value = "Username of the User being looked up", name = "username", type = "integer", required = true,
                    example = "user") @RequestParam("username") String username) {

        return postService.findPostsByAuthor(username)
                .map(postDocument -> modelMapper.map(postDocument, PostModel.class))
                .collectList()
                .map(UsersPostsModel::new)
                .map(ResponseEntity::ok);
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ApiOperation("Create a post")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Successfully created post", response = PostModel.class),
            @ApiResponse(code = 400, message = "Incorrectly entered data", response = ErrorResponse.class)})
    Mono<ResponseEntity<PostModel>> cretePost(
            @ApiParam(value = "The post's photos", name = "photos", required = true)
            @RequestPart(value = "photos", required = false) Flux<FilePart> photos,
            // required = false - Not required to bypass the exception with a missing request part
            // and return a validation failed message
            @ApiParam(value = "The post's data", name = "post")
            @RequestPart(value = "post", required = false) String post,
            @ApiParam(hidden = true) @RequestHeader("Authorization") String authorizationHeader
    ) throws JsonProcessingException {

        return Mono.just(jwtUtil.extractUsernameFromHeader(authorizationHeader))
                .zipWith(postObjectMapper.getPostDTOFromString(post, photos))
                .flatMap((tuple) -> postService.createPost(tuple.getT1(), tuple.getT2()))
                .map(postDocument -> modelMapper.map(postDocument, PostModel.class))
                .map(postModel -> ResponseEntity.created(URI.create("/api/v1/posts/" + postModel.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(postModel));
    }

    @PutMapping(path = "/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ApiOperation(value = "Update a post", notes = "Provide an id")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully updated post", response = PostModel.class),
            @ApiResponse(code = 400, message = "Invalid Post's id supplied or incorrectly entered data"),
            @ApiResponse(code = 404, message = "Could not find Post with provided id", response = ErrorResponse.class)})
    Mono<ResponseEntity<PostModel>> updatePost(
            @ApiParam(value = "Id of the Post being updated", name = "id", type = "string",
                    required = true, example = "id")
            @PathVariable("id") String id,
            @ApiParam(value = "The post's photos", name = "photos", required = true)
            @RequestPart(value = "photos", required = false) Flux<FilePart> photos,
            // required = false - Not required to bypass the exception with a missing request part
            // and return a validation failed message
            @ApiParam(value = "The post's data", name = "post")
            @RequestPart(value = "post", required = false) String post,
            @ApiParam(hidden = true) @RequestHeader("Authorization") String authorizationHeader
    ) {

        return Mono.just(jwtUtil.extractUsernameFromHeader(authorizationHeader))
                .zipWith(postObjectMapper.getPostDTOFromString(post, photos))
                .flatMap((tuple) -> postService.updatePost(id, tuple.getT1(), tuple.getT2()))
                .map(postDocument -> modelMapper.map(postDocument, PostModel.class))
                .map(ResponseEntity::ok);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Added to remove the default 200 status added by Swagger
    @ApiOperation(value = "Delete a post", notes = "Provide an id")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Successfully deleted a post"),
            @ApiResponse(code = 400, message = "Invalid Post's id supplied"),
            @ApiResponse(code = 404, message = "Could not find Post with provided id", response = ErrorResponse.class)})
    Mono<ResponseEntity<Void>> deletePost(
            @ApiParam(value = "Id of the Post being deleted", name = "id", type = "string",
                    required = true, example = "id")
            @PathVariable("id") String id,
            @ApiParam(hidden = true) @RequestHeader("Authorization") String authorizationHeader) {

        return Mono.just(jwtUtil.extractUsernameFromHeader(authorizationHeader))
                .flatMap((username) -> postService.deletePost(id, username))
                .map((postDocumentVoid) -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                        .body(postDocumentVoid));
    }
}
