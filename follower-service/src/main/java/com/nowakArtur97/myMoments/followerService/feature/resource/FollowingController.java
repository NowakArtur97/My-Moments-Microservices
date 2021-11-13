package com.nowakArtur97.myMoments.followerService.feature.resource;

import com.nowakArtur97.myMoments.followerService.advice.ErrorResponse;
import com.nowakArtur97.myMoments.followerService.feature.node.FollowerService;
import io.swagger.annotations.*;
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
@Api(tags = {FollowingTag.RESOURCE})
@ApiResponses(value = {
        @ApiResponse(code = 401, message = "Permission to the resource is prohibited"),
        @ApiResponse(code = 403, message = "Access to the resource is prohibited")})
class FollowingController {

    private final static int DEFAULT_MIN_DEGREE = 2;
    private final static int DEFAULT_MAX_DEGREE = 2;

    private final FollowerService followerService;

    @GetMapping(path = "/{username}")
    @ApiOperation(value = "Find User's Following by Username", notes = "Provide a name to look up specific Following")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully found following", response = UsersAcquaintancesModel.class),
            @ApiResponse(code = 400, message = "Invalid User's name supplied", response = ErrorResponse.class)})
    Mono<ResponseEntity<UsersAcquaintancesModel>> getFollowing(
            @ApiParam(value = "Username of the Following being looked up", name = "username", type = "string",
                    required = true, example = "username")
            @PathVariable("username") @Valid @NotBlankParam(message = "{follower.username.blank}") String username
    ) {

        return followerService.findFollowed(username)
                .map(ResponseEntity::ok);
    }

    @GetMapping(path = "/recommendations/{username}/")
    @ApiOperation(value = "Recommend Users to Follow", notes = "Provide a name, min and max degree to look up Users")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully found Users to recommendUsers", response = UsersAcquaintancesModel.class),
            @ApiResponse(code = 400, message = "Invalid User's name or degree supplied", response = ErrorResponse.class)})
    Mono<ResponseEntity<UsersAcquaintancesModel>> recommendUsers(
            @ApiParam(value = "Username of the recommended Users being looked up", name = "username", type = "string",
                    required = true, example = "username")
            @PathVariable("username") @Valid @NotBlankParam(message = "{follower.username.blank}") String username,
            @ApiParam(value = "Min degree of the recommended Users being looked up", name = "minDegree", type = "integer",
                    required = true, example = "1", defaultValue = "2")
            @RequestParam(name = "minDegree", required = false) Optional<Integer> minDegree,
            @ApiParam(value = "Max degree of the recommended Users being looked up", name = "maxDegree", type = "integer",
                    required = true, example = "1", defaultValue = "2")
            @RequestParam(name = "maxDegree", required = false) Optional<Integer> maxDegree
    ) {

        return followerService.recommendUsers(username, minDegree.orElse(DEFAULT_MIN_DEGREE), maxDegree.orElse(DEFAULT_MAX_DEGREE))
                .map(ResponseEntity::ok);
    }
}
