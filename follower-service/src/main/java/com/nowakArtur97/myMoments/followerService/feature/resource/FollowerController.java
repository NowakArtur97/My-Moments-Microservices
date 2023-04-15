package com.nowakArtur97.myMoments.followerService.feature.resource;

import com.nowakArtur97.myMoments.followerService.feature.node.FollowerService;
import com.nowakArtur97.myMoments.followerService.jwt.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = FollowerTag.RESOURCE, description = FollowerTag.DESCRIPTION)
@ApiResponses(value = {@ApiResponse(responseCode = "401", description = "Permission to the resource is prohibited"), @ApiResponse(responseCode = "403", description = "Access to the resource is prohibited")})
class FollowerController {

    private final FollowerService followerService;

    private final JwtUtil jwtUtil;

    @GetMapping(path = "/{username}")
    @Operation(summary = "Find User's Followers by Username", description = "Provide a name to look up specific Followers")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully found followers"),
            @ApiResponse(responseCode = "400", description = "Invalid User's name supplied")})
    Mono<ResponseEntity<UsersAcquaintancesModel>> getFollowers(
            @Parameter(description = "Username of the Followers being looked up", name = "username", required = true, example = "username")
            @PathVariable("username") @Valid @NotBlankParam(message = "{follower.username.blank}") String username) {

        return followerService.findFollowers(username)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/{username}")
    @Operation(summary = "Follow user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully followed User"),
            @ApiResponse(responseCode = "400", description = "Invalid User's name supplied")})
    Mono<ResponseEntity<Void>> followUser(
            @Parameter(description = "Username of the User being followed", name = "username", required = true, example = "username")
            @PathVariable("username") @Valid @NotBlankParam(message = "{follower.username.blank}") String usernameToFollow,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader) {

        return Mono.just(jwtUtil.extractUsernameFromHeader(authorizationHeader))
                .flatMap((username) -> followerService.followUser(username, usernameToFollow))
                .map((followerVoid) -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(followerVoid));
    }

    @DeleteMapping("/{username}")
    @Operation(summary = "Unfollow user")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Successfully unfollowed User"),
            @ApiResponse(responseCode = "400", description = "Invalid User's name supplied"),
            @ApiResponse(responseCode = "404", description = "Could not find User with provided username")})
    Mono<ResponseEntity<Void>> unfollowUser(
            @Parameter(description = "Username of the User being unfollowed", name = "username", required = true, example = "username")
            @PathVariable("username") @Valid @NotBlankParam(message = "{follower.username.blank}") String usernameToFollow,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader) {

        return Mono.just(jwtUtil.extractUsernameFromHeader(authorizationHeader))
                .flatMap((username) -> followerService.unfollowUser(username, usernameToFollow))
                .map((followerVoid) -> ResponseEntity.noContent().build());
    }
}
