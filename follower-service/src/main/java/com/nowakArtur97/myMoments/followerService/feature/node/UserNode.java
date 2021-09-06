package com.nowakArtur97.myMoments.followerService.feature.node;

import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.Collections;
import java.util.Set;

@Node(primaryLabel = "User")
@Getter
@ToString
class UserNode extends AbstractNode {

    @Id
    @GeneratedValue
    private Long id;

    private final String username;

    @Relationship(type = Relationships.FOLLOWING_RELATIONSHIP)
    private final Set<FriendshipRelationship> friendships;

    public UserNode(String username) {

        this.username = username;
        this.friendships = Collections.emptySet();
    }
}
