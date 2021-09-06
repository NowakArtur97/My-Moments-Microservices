package com.nowakArtur97.myMoments.followerService.feature.node;

import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@RelationshipProperties
class FriendshipRelationship extends AbstractNode {

    @TargetNode
    private UserNode friendNode;
}
