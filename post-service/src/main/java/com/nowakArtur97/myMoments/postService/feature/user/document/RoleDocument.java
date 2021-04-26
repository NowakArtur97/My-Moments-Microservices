package com.nowakArtur97.myMoments.postService.feature.user.document;

import com.nowakArtur97.myMoments.postService.common.document.AbstractDocument;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "role")
@AllArgsConstructor
@Getter
@ToString
public class RoleDocument extends AbstractDocument {

    @Indexed(unique = true)
    private final String name;
}
