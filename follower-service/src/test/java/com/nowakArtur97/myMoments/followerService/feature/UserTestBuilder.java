package com.nowakArtur97.myMoments.followerService.feature;


import com.nowakArtur97.myMoments.followerService.feature.node.FollowingRelationship;
import com.nowakArtur97.myMoments.followerService.feature.node.UserNode;
import com.nowakArtur97.myMoments.followerService.feature.resource.User;
import com.nowakArtur97.myMoments.followerService.feature.resource.UserModel;
import com.nowakArtur97.myMoments.followerService.testUtil.enums.ObjectType;

import java.util.HashSet;
import java.util.Set;

public class UserTestBuilder {

    private String username = "user";

    private Set<FollowingRelationship> following = new HashSet<>();

    private Set<FollowingRelationship> followers = new HashSet<>();

    public UserTestBuilder withUsername(String username) {

        this.username = username;

        return this;
    }

    public UserTestBuilder withFollowing(Set<FollowingRelationship> following) {

        this.following = following;

        return this;
    }

    public UserTestBuilder withFollowers(Set<FollowingRelationship> followers) {

        this.followers = followers;

        return this;
    }

    public User build(ObjectType type) {

        User user;

        switch (type) {

            case NODE:

                user = new UserNode(username, following, followers);

                break;

            case MODEL:

                user = new UserModel(username, following.size(), followers.size());

                break;

            default:
                throw new RuntimeException("The specified type does not exist");
        }

        resetProperties();

        return user;
    }

    private void resetProperties() {

        username = "user";

        following = new HashSet<>();

        followers = new HashSet<>();
    }
}
