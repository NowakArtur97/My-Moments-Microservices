package com.nowakArtur97.myMoments.userService.feature.authentication;


import com.nowakArtur97.myMoments.userService.common.model.ErrorResponse;
import com.nowakArtur97.myMoments.userService.common.util.JwtUtil;
import com.nowakArtur97.myMoments.userService.feature.document.CustomUserDetailsService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/authentication")
@RequiredArgsConstructor
@Api(tags = {AuthenticationTag.RESOURCE})
@ApiResponses(value = {
        @ApiResponse(code = 401, message = "Permission to the resource is prohibited"),
        @ApiResponse(code = 403, message = "Access to the resource is prohibited")})
@RefreshScope
@Slf4j
class AuthenticationController {

    @Value("${my-moments.jwt.validity:36000000}")
    private long validity;

    private final CustomUserDetailsService customUserDetailsService;

    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;

    @PostMapping
    @ApiOperation(value = "Generate API key", notes = "Generate API key")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully generated API key", response = AuthenticationResponse.class),
            @ApiResponse(code = 400, message = "Incorrectly entered data", response = ErrorResponse.class)})
    ResponseEntity<AuthenticationResponse> loginUser(@RequestBody @ApiParam(value = "User credentials", name = "user",
            required = true) AuthenticationRequest authenticationRequest) {

        String userNameOrEmail = authenticationRequest.getUsername() != null
                ? authenticationRequest.getUsername()
                : authenticationRequest.getEmail();

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(userNameOrEmail);

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                userNameOrEmail, authenticationRequest.getPassword()));

        String token = jwtUtil.generateToken(userDetails);

        log.info("Generating token for user: {}", userDetails.getUsername());

        return ResponseEntity.ok(new AuthenticationResponse(token, validity));
    }
}
