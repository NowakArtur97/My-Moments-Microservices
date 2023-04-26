import { Injectable } from '@angular/core';
import { Resolve } from '@angular/router';

import Post from '../models/post.model';
import { PostService } from '../services/post.service';

@Injectable({ providedIn: 'root' })
export default class MyPostsResolver implements Resolve<any> {
  constructor(private postService: PostService) {}

  public resolve(): Post[] {
    const posts = this.postService.myPosts.getValue();
    if (posts.length === 0) {
      this.postService.getMyPosts();
      return this.postService.myPosts.getValue();
    } else {
      return posts;
    }
  }
}
