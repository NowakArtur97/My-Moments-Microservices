package com.nowakArtur97.myMoments.followerService.feature.node;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.UUID;

@Node
@EqualsAndHashCode(of = "uuid")
@NoArgsConstructor
abstract class AbstractNode {

    @Id
    @GeneratedValue
    private Long id;

    private String uuid = UUID.randomUUID().toString();
//
//    @CreatedDate
//    private Date createDate;
//
//    @LastModifiedDate
//    private Date modifyDate;
//
//    @Version
//    private Long version;
}
