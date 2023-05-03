import { Injectable } from '@angular/core';
import { FollowerService } from 'src/app/follower/service/follower.service';

import Post from '../models/post.model';
import { PostService } from '../services/post.service';
import PostsResolver from './posts.resolver';

@Injectable({ providedIn: 'root' })
export default class FollowingResolver extends PostsResolver {
  constructor(
    protected postService: PostService,
    private followerService: FollowerService
  ) {
    super(postService);
  }

  getPosts = (): Post[] => this.postService.followingPosts.getValue();

  fetchPosts(): void {
    const usernames = this.followerService.myFollowing
      .getValue()
      .map(({ username }) => username);
    this.postService.getFollowingPosts(usernames);
  }
}
