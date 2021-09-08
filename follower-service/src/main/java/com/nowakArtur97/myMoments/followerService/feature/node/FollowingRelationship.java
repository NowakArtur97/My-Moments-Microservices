package com.nowakArtur97.myMoments.followerService.feature.node;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@RelationshipProperties
@NoArgsConstructor
@AllArgsConstructor
@Getter
class FollowingRelationship extends AbstractNode {

    @TargetNode
    private UserNode followerNode;
}