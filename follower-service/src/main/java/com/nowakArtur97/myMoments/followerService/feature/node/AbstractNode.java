package com.nowakArtur97.myMoments.followerService.feature.node;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;

import java.util.Date;
import java.util.UUID;

@EqualsAndHashCode(of = "uuid")
@ToString
abstract class AbstractNode {

    private String uuid = UUID.randomUUID().toString();

    @CreatedDate
    private Date createDate;

    @LastModifiedDate
    private Date modifyDate;

    @Version
    private Integer version;
}
