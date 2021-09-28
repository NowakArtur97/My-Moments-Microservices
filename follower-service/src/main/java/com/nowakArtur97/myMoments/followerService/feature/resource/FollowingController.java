package com.nowakArtur97.myMoments.followerService.feature.resource;

import com.nowakArtur97.myMoments.followerService.advice.ErrorResponse;
import com.nowakArtur97.myMoments.followerService.feature.node.FollowerService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/following")
@Validated
@RequiredArgsConstructor
@Api(tags = {FollowerTag.RESOURCE})
@ApiResponses(value = {
        @ApiResponse(code = 401, message = "Permission to the resource is prohibited"),
        @ApiResponse(code = 403, message = "Access to the resource is prohibited")})
class FollowingController {

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
}
