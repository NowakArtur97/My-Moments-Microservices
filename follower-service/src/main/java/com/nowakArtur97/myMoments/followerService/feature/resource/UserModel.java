package com.nowakArtur97.myMoments.followerService.feature.resource;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Schema(description = "Details about the User")
public class UserModel implements User {

    @Schema(accessMode = READ_ONLY, description = "The user's name")
    private String username;
}
