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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}
