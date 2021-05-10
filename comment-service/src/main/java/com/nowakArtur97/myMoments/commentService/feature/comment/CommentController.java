package com.nowakArtur97.myMoments.commentService.feature.comment;

import com.nowakArtur97.myMoments.commentService.common.model.ErrorResponse;
import com.nowakArtur97.myMoments.commentService.common.util.JwtUtil;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
public class CommentController {

    private final CommentService commentService;

    private final JwtUtil jwtUtil;

    private final ModelMapper modelMapper;

    @PostMapping(path = "/comments")
    @ApiOperation(value = "Add a comment to the post", notes = "Provide a Post's id")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Successfully added comment", response = CommentModel.class),
            @ApiResponse(code = 400, message = "Incorrectly entered data", response = ErrorResponse.class),
            @ApiResponse(code = 404, message = "Could not find Post with provided id", response = ErrorResponse.class)})
    Mono<ResponseEntity<CommentModel>> addCommentToPost(
            @ApiParam(value = "Id of the Post being commented", name = "postId", type = "integer", required = true, example = "1")
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
}
