package com.nowakArtur97.myMoments.followerService.feature.node;

import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@RelationshipProperties
@NoArgsConstructor
class FriendshipRelationship extends AbstractNode {

    @TargetNode
    private UserNode friendNode;

    public FriendshipRelationship(UserNode friendNode) {
        this.friendNode = friendNode;
    }
}
