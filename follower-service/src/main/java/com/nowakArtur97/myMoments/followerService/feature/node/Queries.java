package com.nowakArtur97.myMoments.followerService.feature.node;

final class Queries {

    private static final String USER_MATCH = "MATCH (u:User {username: $username})-[:";
    private static final String USERS_RETURN = "]->(f:User) RETURN f";

    public static final String FIND_FOLLOWERS = USER_MATCH + Relationships.FOLLOWED_RELATIONSHIP + USERS_RETURN;

    public static final String FIND_FOLLOWED = USER_MATCH + Relationships.FOLLOWING_RELATIONSHIP + USERS_RETURN;

    public static final String FOLLOW = "MATCH (u:User {username: $username}) " +
            "MATCH (f:User {username: $usernameToFollow}) " +
            "MERGE (u)-[:IS_FOLLOWING]->(f) " +
            "MERGE (f)-[:IS_FOLLOWED]->(u)";

    public static final String UNFOLLOW = "MATCH (u:User {username: $username})" +
            "-[fr:IS_FOLLOWING]->" +
            "(f:User {username: $usernameToUnfollow})" +
            "-[fr2:IS_FOLLOWED]->" +
            "(u:User {username: $username}) " +
            "DELETE fr,fr2";

    public static final String RECOMMEND = "MATCH (u:User {username: $username})" +
            "-[:IS_FOLLOWING* $minDegree .. $maxDegree]->(f:User) " +
            "WHERE NOT (u)-[:IS_FOLLOWING]->(f) " +
            "AND u <> f " +
            "RETURN f, COUNT(*) " +
            "ORDER BY COUNT(*) DESC, f";
}
