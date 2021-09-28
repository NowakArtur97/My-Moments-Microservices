package com.nowakArtur97.myMoments.followerService.feature.node;

final class Queries {

    private static final String USER_MATCH = "MATCH (u:User {username: $username})-[:";
    private static final String USERS_RETURN = "]->(f:User) RETURN f";

    public static final String FIND_FOLLOWERS = USER_MATCH + Relationships.FOLLOWED_RELATIONSHIP + USERS_RETURN;

    public static final String FIND_FOLLOWED = USER_MATCH + Relationships.FOLLOWING_RELATIONSHIP + USERS_RETURN;
}
