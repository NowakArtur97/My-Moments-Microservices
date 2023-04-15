package com.nowakArtur97.myMoments.followerService.feature.resource;

import com.nowakArtur97.myMoments.followerService.feature.node.FollowerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/following")
@Validated
@RequiredArgsConstructor
@Tag(name = FollowingTag.RESOURCE, description = FollowingTag.DESCRIPTION)
@ApiResponses(value = {
        @ApiResponse(responseCode = "401", description = "Permission to the resource is prohibited"),
        @ApiResponse(responseCode = "403", description = "Access to the resource is prohibited")})
class FollowingController {

    private final static int DEFAULT_MIN_DEGREE = 2;
    private final static int DEFAULT_MAX_DEGREE = 2;

    private final FollowerService followerService;

    @GetMapping(path = "/{username}")
    @Operation(summary = "Find User's Following by Username", description = "Provide a name to look up specific Following")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully found following"),
            @ApiResponse(responseCode = "400", description = "Invalid User's name supplied")})
    Mono<ResponseEntity<UsersAcquaintancesModel>> getFollowing(
            @Parameter(description = "Username of the Following being looked up", name = "username", required = true, example = "username")
            @PathVariable("username") @Valid @NotBlankParam(message = "{follower.username.blank}") String username
    ) {

        return followerService.findFollowed(username)
                .map(ResponseEntity::ok);
    }

    @GetMapping(path = "/recommendations/{username}")
    @Operation(summary = "Recommend Users to Follow", description = "Provide a name, min and max degree to look up Users")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully found Users to recommendUsers"),
            @ApiResponse(responseCode = "400", description = "Invalid User's name or degree supplied")})
    Mono<ResponseEntity<UsersAcquaintancesModel>> recommendUsers(
            @Parameter(description = "Username of the recommended Users being looked up", name = "username",
                    required = true, example = "username")
            @PathVariable("username") @Valid @NotBlankParam(message = "{follower.username.blank}") String username,
            @Parameter(description = "Min degree of the recommended Users being looked up", name = "minDegree",
                    required = true, example = "1")
            @RequestParam(name = "minDegree", required = false) Optional<Integer> minDegree,
            @Parameter(description = "Max degree of the recommended Users being looked up", name = "maxDegree",
                    required = true, example = "1")
            @RequestParam(name = "maxDegree", required = false) Optional<Integer> maxDegree
    ) {

        return followerService.recommendUsers(username, minDegree.orElse(DEFAULT_MIN_DEGREE), maxDegree.orElse(DEFAULT_MAX_DEGREE))
                .map(ResponseEntity::ok);
    }
}
