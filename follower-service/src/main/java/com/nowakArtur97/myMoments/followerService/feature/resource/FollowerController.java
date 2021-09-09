package com.nowakArtur97.myMoments.followerService.feature.resource;

import com.nowakArtur97.myMoments.followerService.advice.ErrorResponse;
import com.nowakArtur97.myMoments.followerService.feature.node.FollowerService;
import com.nowakArtur97.myMoments.followerService.jwt.JwtUtil;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/followers")
@Validated
@RequiredArgsConstructor
@Api(tags = {FollowerTag.RESOURCE})
@ApiResponses(value = {
        @ApiResponse(code = 401, message = "Permission to the resource is prohibited"),
        @ApiResponse(code = 403, message = "Access to the resource is prohibited")})
class FollowerController {

    private final FollowerService followerService;

    private final JwtUtil jwtUtil;

    @PostMapping("/{username}")
    @ApiOperation("Follow user")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully followed User"),
            @ApiResponse(code = 400, message = "Invalid User's name supplied", response = ErrorResponse.class)})
    Mono<ResponseEntity<Void>> followUser(
            @ApiParam(value = "Username of the User being followed", name = "username", type = "string",
                    required = true, example = "username")
            @PathVariable("username") @Valid @NotBlankParam(message = "{follower.username.blank}") String usernameToFollow,
            @ApiParam(hidden = true) @RequestHeader("Authorization") String authorizationHeader) {

        return Mono.just(jwtUtil.extractUsernameFromHeader(authorizationHeader))
                .flatMap((username) -> followerService.followUser(username, usernameToFollow))
                .map((followerVoid) -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(followerVoid));
    }

    @DeleteMapping("/{username}")
    @ApiOperation("Unfollow user")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Successfully unfollowed User"),
            @ApiResponse(code = 400, message = "Invalid User's name supplied", response = ErrorResponse.class),
            @ApiResponse(code = 404, message = "Could not find User with provided username", response = ErrorResponse.class)})
    Mono<ResponseEntity<Void>> unfollowUser(
            @ApiParam(value = "Username of the User being unfollowed", name = "username", type = "string",
                    required = true, example = "username")
            @PathVariable("username") @Valid @NotBlankParam(message = "{follower.username.blank}") String usernameToFollow,
            @ApiParam(hidden = true) @RequestHeader("Authorization") String authorizationHeader) {

        return Mono.just(jwtUtil.extractUsernameFromHeader(authorizationHeader))
                .flatMap((username) -> followerService.unfollowUser(username, usernameToFollow))
                .map((followerVoid) -> ResponseEntity.noContent().build());
    }
}
