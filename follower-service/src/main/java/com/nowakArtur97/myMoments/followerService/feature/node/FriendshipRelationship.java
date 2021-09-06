package com.nowakArtur97.myMoments.followerService.feature.node;

import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@RelationshipProperties
class FriendshipRelationship extends AbstractNode {

    @Id
    @GeneratedValue
    @Setter
    private Long id;

    @TargetNode
    private final UserNode friendNode;

    public FriendshipRelationship(UserNode friendNode) {
        this.friendNode = friendNode;
    }
}
