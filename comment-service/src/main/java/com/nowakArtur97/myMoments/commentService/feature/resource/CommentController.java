package com.nowakArtur97.myMoments.commentService.feature.resource;

import com.nowakArtur97.myMoments.commentService.feature.document.CommentService;
import com.nowakArtur97.myMoments.commentService.jwt.JwtUtil;
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
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/v1/posts/{postId}/")
@RequiredArgsConstructor
@Tag(name = CommentTag.RESOURCE, description = CommentTag.DESCRIPTION)
@ApiResponses(value = {
        @ApiResponse(responseCode = "401", description = "Permission to the resource is prohibited"),
        @ApiResponse(responseCode = "403", description = "Access to the resource is prohibited")})
class CommentController {

    private final CommentService commentService;

    private final JwtUtil jwtUtil;

    private final ModelMapper modelMapper;

    @GetMapping(path = "/comments")
    @Operation(summary = "Find Post's Comments by Post Id", description = "Provide a Post's id to look up specific Comments",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Post's comments found"),
            @ApiResponse(responseCode = "400", description = "Invalid Post's id supplied")})
    Mono<ResponseEntity<PostsCommentsModel>> getPostsComments(
            @Parameter(description = "Id of the Post's Comments being looked up", name = "postId",
                    required = true, example = "id")
            @PathVariable("postId") String postId) {

        return commentService.findCommentsByRelatedPost(postId)
                .map(commentDocument -> modelMapper.map(commentDocument, CommentModel.class))
                .collectList()
                .map(PostsCommentsModel::new)
                .map(ResponseEntity::ok);
    }

    @PostMapping(path = "/comments")
    @Operation(summary = "Add a comment to the post", description = "Provide a Post's id",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Successfully added comment"),
            @ApiResponse(responseCode = "400", description = "Incorrectly entered data"),
            @ApiResponse(responseCode = "404", description = "Could not find Post with provided id")})
    Mono<ResponseEntity<CommentModel>> addCommentToPost(
            @Parameter(description = "Id of the Post being commented", name = "postId", required = true, example = "id")
            @PathVariable("postId") String postId,
            @Parameter(description = "Comment content", name = "comment", required = true) @RequestBody @Valid CommentDTO commentDTO,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader) {

        return Mono.just(jwtUtil.extractUsernameFromHeader(authorizationHeader))
                .flatMap(username -> commentService.addComment(postId, username, commentDTO))
                .map(commentDocument -> modelMapper.map(commentDocument, CommentModel.class))
                .map(commentModel -> ResponseEntity.created(URI.create("/api/v1/posts/" + commentModel.getId() + "/comments"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(commentModel));
    }

    @PutMapping(path = "/comments/{id}")
    @Operation(summary = "Update a comment in the post", description = "Provide a Post's and Comment's ids",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully updated comment"),
            @ApiResponse(responseCode = "400", description = "Invalid Post's or Comment's id supplied or incorrectly entered data"),
            @ApiResponse(responseCode = "404", description = "Could not find Post or Comment with provided ids")})
    Mono<ResponseEntity<CommentModel>> updateCommentInPost(
            @Parameter(description = "Id of the Post Comment's being updated", name = "postId", required = true, example = "id")
            @PathVariable("postId") String postId,
            @Parameter(description = "Id of the Comment being updated", name = "id", required = true, example = "id")
            @PathVariable("id") String id,
            @Parameter(description = "Updated comment content", name = "comment", required = true) @RequestBody @Valid CommentDTO commentDTO,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader
    ) {

        return Mono.just(jwtUtil.extractUsernameFromHeader(authorizationHeader))
                .flatMap(username -> commentService.updateComment(id, postId, username, commentDTO))
                .map(commentDocument -> modelMapper.map(commentDocument, CommentModel.class))
                .map(ResponseEntity::ok);
    }

    @DeleteMapping(path = "/comments/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Added to remove the default 200 status added by Swagger
    @Operation(summary = "Delete a comment", description = "Provide a Post's and Comment's ids",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Successfully deleted a comment"),
            @ApiResponse(responseCode = "400", description = "Invalid Post's or Comment's id supplied"),
            @ApiResponse(responseCode = "404", description = "Could not find Post or Comment with provided ids")})
    Mono<ResponseEntity<Void>> deleteCommentInPost(
            @Parameter(description = "Id of the Post Comment's being deleted", name = "postId", required = true, example = "id")
            @PathVariable("postId") String postId,
            @Parameter(description = "Id of the Comment being deleted", name = "id", required = true, example = "id")
            @PathVariable("id") String id,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader) {

        return Mono.just(jwtUtil.extractUsernameFromHeader(authorizationHeader))
                .flatMap((username) -> commentService.deleteComment(id, postId, username))
                .map((commentDocumentVoid) -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                        .body(commentDocumentVoid));
    }
}
