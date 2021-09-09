package com.nowakArtur97.myMoments.followerService.feature.resource;

import com.nowakArtur97.myMoments.followerService.advice.ErrorResponse;
import com.nowakArtur97.myMoments.followerService.feature.node.FollowerService;
import com.nowakArtur97.myMoments.followerService.jwt.JwtUtil;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/followers")
@RequiredArgsConstructor
@Api(tags = {FollowerTag.RESOURCE})
@ApiResponses(value = {
        @ApiResponse(code = 401, message = "Permission to the resource is prohibited"),
        @ApiResponse(code = 403, message = "Access to the resource is prohibited")})
class FollowerController {

    private final FollowerService followerService;

    private final JwtUtil jwtUtil;

    @PostMapping
    @ApiOperation("Follow user")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully followed User"),
            @ApiResponse(code = 400, message = "Incorrectly entered follower's data", response = ErrorResponse.class)})
    Mono<ResponseEntity<Void>> followUser(
            @ApiParam(value = "The follower's data", name = "userToFollow", type = "string",
                    required = true) @RequestBody FollowerDTO userToFollow,
            @ApiParam(hidden = true) @RequestHeader("Authorization") String authorizationHeader) {

        return Mono.just(jwtUtil.extractUsernameFromHeader(authorizationHeader))
                .flatMap((username) -> followerService.followUser(username, userToFollow))
                .map((followerDocumentVoid) -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                        .body(followerDocumentVoid));
    }
}
