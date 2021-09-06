package com.nowakArtur97.myMoments.followerService.feature.node;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.Date;
import java.util.UUID;

@Node
@EqualsAndHashCode(of = "uuid")
@Getter
@Setter(AccessLevel.PRIVATE)
@ToString
abstract class AbstractNode {

    @Id
    @GeneratedValue
    @Setter
    private Long id;

    private String uuid = UUID.randomUUID().toString();

    @CreatedDate
    private Date createDate;

    @LastModifiedDate
    private Date modifyDate;

    @Version
    private Integer version;
}
