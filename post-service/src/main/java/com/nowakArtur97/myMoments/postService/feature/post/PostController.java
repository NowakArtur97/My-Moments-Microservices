package com.nowakArtur97.myMoments.postService.feature.post;

import com.nowakArtur97.myMoments.postService.common.model.ErrorResponse;
import com.nowakArtur97.myMoments.postService.common.util.JwtUtil;
import com.nowakArtur97.myMoments.postService.exception.ResourceNotFoundException;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
            @ApiResponse(code = 200, message = "Successfully found post", response = PostModel.class),
            @ApiResponse(code = 404, message = "Could not find Post with provided id", response = ErrorResponse.class)})
    Mono<ResponseEntity<PostModel>> getPost(
            @ApiParam(value = "Id of the Post being looked up", name = "id", type = "string",
                    required = true, example = "id") @PathVariable("id") String id
    ) {

        return postService.findPostById(id)
                .map(postDocument -> modelMapper.map(postDocument, PostModel.class))
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Post", id)));
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ApiOperation("Create a post")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Successfully created post", response = PostModel.class),
            @ApiResponse(code = 400, message = "Incorrectly entered data", response = ErrorResponse.class)})
    Mono<ResponseEntity<PostModel>> cretePost(
            @ApiParam(value = "The post's photos", name = "photos", required = true)
            @RequestPart(value = "photos", required = false) Flux<FilePart> photos,
            @ApiParam(value = "The post's data", name = "post") @RequestPart(value = "post", required = false) String post,
            @ApiParam(hidden = true) @RequestHeader("Authorization") String authorizationHeader
    ) {

        return Mono.just(jwtUtil.extractUsernameFromHeader(authorizationHeader))
                        .zipWith(postObjectMapper.getPostDTOFromString(post, photos))
                        .flatMap((tuple) -> postService.createPost(tuple.getT2(), tuple.getT1()))
                        .map(postDocument -> modelMapper.map(postDocument, PostModel.class))
                        .map(postModel -> ResponseEntity.created(URI.create("/api/v1/posts/" + postModel.getId()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(postModel));
    }
}
