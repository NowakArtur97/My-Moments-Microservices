package com.nowakArtur97.myMoments.gatewayService.user;

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
@Setter(AccessLevel.PRIVATE)
@ToString
abstract class AbstractDocument {

    @Id
    @Setter
    private String id;

    private String uuid = UUID.randomUUID().toString();

    @CreatedDate
    private Date createDate;

    @LastModifiedDate
    private Date modifyDate;

    @Version
    private Integer version;
}
