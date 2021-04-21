package com.nowakArtur97.myMoments.userService.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "role")
@AllArgsConstructor
@Getter
@ToString
class RoleDocument extends AbstractDocument {

    private final String name;
}
