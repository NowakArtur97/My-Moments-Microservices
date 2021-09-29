package com.nowakArtur97.myMoments.followerService.feature.node;

import com.nowakArtur97.myMoments.followerService.feature.resource.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@Node(primaryLabel = "User")
@NoArgsConstructor
@Getter
@ToString
public class UserNode extends AbstractNode implements User {

    private String username;

    @Relationship(type = Relationships.FOLLOWING_RELATIONSHIP)
    private Set<FollowingRelationship> following;

    @Relationship(type = Relationships.FOLLOWED_RELATIONSHIP)
    private Set<FollowingRelationship> followers;

    public UserNode(String username) {

        this();
        this.username = username;
        this.following = new HashSet<>();
        this.followers = new HashSet<>();
    }

    public UserNode(String username, Set<FollowingRelationship> following, Set<FollowingRelationship> followers) {

        this();
        this.username = username;
        this.following = following;
        this.followers = followers;
    }
}
