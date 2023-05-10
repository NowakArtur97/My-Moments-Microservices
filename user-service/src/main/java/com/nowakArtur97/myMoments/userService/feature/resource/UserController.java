package com.nowakArtur97.myMoments.userService.feature.resource;

import com.nowakArtur97.myMoments.userService.common.model.ErrorResponse;
import com.nowakArtur97.myMoments.userService.common.util.JwtUtil;
import com.nowakArtur97.myMoments.userService.feature.authentication.AuthenticationResponse;
import com.nowakArtur97.myMoments.userService.feature.document.CustomUserDetailsService;
import com.nowakArtur97.myMoments.userService.feature.document.UserDocument;
import com.nowakArtur97.myMoments.userService.feature.document.UserService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Api(tags = {UserTag.RESOURCE})
@ApiResponses(value = {
        @ApiResponse(code = 401, message = "Permission to the resource is prohibited"),
        @ApiResponse(code = 403, message = "Access to the resource is prohibited")})
@RefreshScope
@Slf4j
class UserController {

    @Value("${my-moments.jwt.validity:36000000}")
    private long validity;

    private final UserService userService;

    private final CustomUserDetailsService customUserDetailsService;

    private final JwtUtil jwtUtil;

    private final UserObjectMapper userObjectMapper;

    private final ModelMapper modelMapper;

    @GetMapping(path = "/photos")
    @ApiOperation("Get Users photos by Usernames")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully found photos")})
    ResponseEntity<UsersPhotosModel> getUserPhotos(
            @ApiParam(value = "Usernames", name = "usernames", required = true)
            @RequestParam("usernames") List<String> usernames
    ) {

        List<UserPhotoModel> usersPhotosData = userService.findUsersPhotosByUsernames(usernames);
        return new ResponseEntity<>(new UsersPhotosModel(usersPhotosData), HttpStatus.OK);
    }

    @PutMapping(path = "/me", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    @ApiOperation("Update an account")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully updated account", response = UserModel.class),
            @ApiResponse(code = 400, message = "Invalid User's token supplied or incorrectly entered data"),
            @ApiResponse(code = 404, message = "Could not find User with provided token", response = ErrorResponse.class)})
    ResponseEntity<UserModel> updateUser(
            @ApiParam(value = "The user's data", name = "user", required = true)
            @RequestPart(value = "user", required = false) String user,
            // required = false - Not required to bypass the exception with a missing request part and return a validation failed message
            @ApiParam(value = "The user's image", name = "image")
            @RequestPart(value = "image", required = false) MultipartFile image,
            @ApiParam(hidden = true) @RequestHeader("Authorization") String authorizationHeader) throws IOException {

        UserUpdateDTO userUpdateDTO = (UserUpdateDTO) userObjectMapper.getUserDTOFromString(user, UserUpdateDTO.class);

        String username = jwtUtil.extractUsernameFromHeader(authorizationHeader);

        UserDocument updatedUserDocument = userService.updateUser(username, userUpdateDTO, image);

        UserDetails userDetails = new User(updatedUserDocument.getUsername(), updatedUserDocument.getPassword(),
                customUserDetailsService.getAuthorities(updatedUserDocument.getRoles()));

        log.info("Generating token for an updated user: {}", userDetails.getUsername());

        String newToken = jwtUtil.generateToken(userDetails);

        UserModel userModel = modelMapper.map(updatedUserDocument, UserModel.class);
        userModel.setAuthenticationResponse(new AuthenticationResponse(newToken, validity));

        return new ResponseEntity<>(userModel, HttpStatus.OK);
    }

    @DeleteMapping(path = "/me")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Added to remove the default 200 status added by Swagger
    @ApiOperation("Delete an account")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Successfully deleted an account"),
            @ApiResponse(code = 400, message = "Invalid User's token supplied"),
            @ApiResponse(code = 404, message = "Could not find User with provided token", response = ErrorResponse.class)})
    ResponseEntity<Void> deleteUser(
            @ApiParam(hidden = true) @RequestHeader("Authorization") String authorizationHeader) {

        String username = jwtUtil.extractUsernameFromHeader(authorizationHeader);

        userService.deleteUser(username);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
