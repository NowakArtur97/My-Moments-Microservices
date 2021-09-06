package com.nowakArtur97.myMoments.followerService.feature.node;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.Set;

@Node(primaryLabel = "User")
@AllArgsConstructor
@Getter
@Setter
@ToString
class UserNode extends AbstractNode {

    private String username;

    @Relationship(type = Relationships.FOLLOWING_RELATIONSHIP)
    private Set<FriendshipRelationship> friendshipRelationships;
}
