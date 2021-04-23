package com.nowakArtur97.myMoments.userService.feature.user.document;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.UUID;

@Document
@EqualsAndHashCode(of = "uuid")
@Getter
@Setter
@ToString
abstract class AbstractDocument {

    @Id
    private String id;

    @Setter(AccessLevel.PRIVATE)
    private String uuid = UUID.randomUUID().toString();

    @CreatedDate
    private Date createDate;

    @LastModifiedDate
    private Date modifyDate;

    @Version
    private Integer version;
}
