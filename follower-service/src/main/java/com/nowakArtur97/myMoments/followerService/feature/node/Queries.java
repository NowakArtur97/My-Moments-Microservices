package com.nowakArtur97.myMoments.followerService.feature.node;

final class Queries {

    public static final String FIND_FOLLOWERS = "MATCH (u:User {username: $username})-[:"
            + Relationships.FOLLOWED_RELATIONSHIP + "]->(f:User) RETURN f";
}
