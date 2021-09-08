package com.nowakArtur97.myMoments.followerService.feature.node;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@Node(primaryLabel = "User")
@Getter
@Setter
@ToString
class UserNode {//extends AbstractNode {

    @Id
    @GeneratedValue
    private Long id;

    private String username;

    @Relationship(type = "IS_FOLLOWING")
    private Set<FollowingRelationship> following;

    @Relationship(type = "IS_FOLLOWED")
    private Set<FollowingRelationship> followers;

    public UserNode() {

        super();
        this.following = new HashSet<>();
        this.followers = new HashSet<>();
    }

    public UserNode(String username) {

        this();
        this.username = username;
    }
}
