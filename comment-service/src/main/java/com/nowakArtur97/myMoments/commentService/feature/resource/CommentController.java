package com.nowakArtur97.myMoments.commentService.feature.resource;

import com.nowakArtur97.myMoments.commentService.advice.ErrorResponse;
import com.nowakArtur97.myMoments.commentService.feature.document.CommentService;
import com.nowakArtur97.myMoments.commentService.jwt.JwtUtil;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/v1/posts/{postId}/")
@RequiredArgsConstructor
@Api(tags = {CommentTag.RESOURCE})
@ApiResponses(value = {
        @ApiResponse(code = 401, message = "Permission to the resource is prohibited"),
        @ApiResponse(code = 403, message = "Access to the resource is prohibited")})
class CommentController {

    private final CommentService commentService;

    private final JwtUtil jwtUtil;

    private final ModelMapper modelMapper;

    @GetMapping(path = "/comments")
    @ApiOperation(value = "Find Post's Comments by Post Id", notes = "Provide a Post's id to look up specific Comments")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Post's comments found", response = PostsCommentsModel.class),
            @ApiResponse(code = 404, message = "Invalid Post's id supplied", response = ErrorResponse.class)})
    Mono<ResponseEntity<PostsCommentsModel>> getPostsComments(
            @ApiParam(value = "Id of the Post's Comments being looked up", name = "postId", type = "string",
                    required = true, example = "id")
            @PathVariable("postId") String postId) {

        return commentService.findCommentsByRelatedPost(postId)
                .map(commentDocument -> modelMapper.map(commentDocument, CommentModel.class))
                .collectList()
                .map(PostsCommentsModel::new)
                .map(ResponseEntity::ok);
    }

    @PostMapping(path = "/comments")
    @ApiOperation(value = "Add a comment to the post", notes = "Provide a Post's id")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Successfully added comment", response = CommentModel.class),
            @ApiResponse(code = 400, message = "Incorrectly entered data", response = ErrorResponse.class),
            @ApiResponse(code = 404, message = "Could not find Post with provided id", response = ErrorResponse.class)})
    Mono<ResponseEntity<CommentModel>> addCommentToPost(
            @ApiParam(value = "Id of the Post being commented", name = "postId", type = "string", required = true, example = "id")
            @PathVariable("postId") String postId,
            @ApiParam(value = "Comment content", name = "comment", required = true) @RequestBody @Valid CommentDTO commentDTO,
            @ApiParam(hidden = true) @RequestHeader("Authorization") String authorizationHeader) {

        return Mono.just(jwtUtil.extractUsernameFromHeader(authorizationHeader))
                .flatMap(username -> commentService.addComment(postId, username, commentDTO))
                .map(commentDocument -> modelMapper.map(commentDocument, CommentModel.class))
                .map(commentModel -> ResponseEntity.created(URI.create("/api/v1/posts/" + commentModel.getId() + "/comments"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(commentModel));
    }

    @PutMapping(path = "/comments/{id}")
    @ApiOperation(value = "Update a comment in the post", notes = "Provide a Post's and Comment's ids")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully updated comment", response = CommentModel.class),
            @ApiResponse(code = 400, message = "Invalid Post's or Comment's id supplied or incorrectly entered data"),
            @ApiResponse(code = 404, message = "Could not find Post or Comment with provided ids", response = ErrorResponse.class)})
    Mono<ResponseEntity<CommentModel>> updateCommentInPost(
            @ApiParam(value = "Id of the Post Comment's being updated", name = "postId", type = "string", required = true, example = "id")
            @PathVariable("postId") String postId,
            @ApiParam(value = "Id of the Comment being updated", name = "id", type = "string", required = true, example = "id")
            @PathVariable("id") String id,
            @ApiParam(value = "Updated comment content", name = "comment", required = true) @RequestBody @Valid CommentDTO commentDTO,
            @ApiParam(hidden = true) @RequestHeader("Authorization") String authorizationHeader
    ) {

        return Mono.just(jwtUtil.extractUsernameFromHeader(authorizationHeader))
                .flatMap(username -> commentService.updateComment(id, postId, username, commentDTO))
                .map(commentDocument -> modelMapper.map(commentDocument, CommentModel.class))
                .map(ResponseEntity::ok);
    }

    @DeleteMapping(path = "/comments/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Added to remove the default 200 status added by Swagger
    @ApiOperation(value = "Delete a comment", notes = "Provide a Post's and Comment's ids")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Successfully deleted a comment"),
            @ApiResponse(code = 400, message = "Invalid Post's or Comment's id supplied"),
            @ApiResponse(code = 404, message = "Could not find Post or Comment with provided ids", response = ErrorResponse.class)})
    Mono<ResponseEntity<Void>> deleteCommentInPost(
            @ApiParam(value = "Id of the Post Comment's being deleted", name = "postId", type = "string", required = true, example = "id")
            @PathVariable("postId") String postId,
            @ApiParam(value = "Id of the Comment being deleted", name = "id", type = "string", required = true, example = "id")
            @PathVariable("id") String id,
            @ApiParam(hidden = true) @RequestHeader("Authorization") String authorizationHeader) {

        return Mono.just(jwtUtil.extractUsernameFromHeader(authorizationHeader))
                .flatMap((username) -> commentService.deleteComment(id, postId, username))
                .map((commentDocumentVoid) -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                        .body(commentDocumentVoid));
    }
}
