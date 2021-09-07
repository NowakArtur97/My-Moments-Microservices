package com.nowakArtur97.myMoments.followerService.feature.node;

import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@RelationshipProperties
@NoArgsConstructor
class FollowingRelationship extends AbstractNode {

    @TargetNode
    private UserNode followerNode;

    public FollowingRelationship(UserNode followerNode) {
        this.followerNode = followerNode;
    }
}
