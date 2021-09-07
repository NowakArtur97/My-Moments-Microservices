package com.nowakArtur97.myMoments.followerService.feature.node;

import lombok.Getter;
import lombok.ToString;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@Node(primaryLabel = "User")
@Getter
@ToString
class UserNode extends AbstractNode {

    private String username;

    @Relationship(type = Relationships.FOLLOWING_RELATIONSHIP)
    private final Set<FollowingRelationship> following;

    public UserNode() {

        this.following = new HashSet<>();
    }

    public UserNode(String username) {

        this();
        this.username = username;
    }
}
