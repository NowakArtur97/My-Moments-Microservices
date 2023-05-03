import { Injectable } from '@angular/core';

import Post from '../models/post.model';
import { PostService } from '../services/post.service';
import PostsResolver from './posts.resolver';

@Injectable({ providedIn: 'root' })
export default class MyPostsResolver extends PostsResolver {
  constructor(protected postService: PostService) {
    super(postService);
  }

  getPosts = (): Post[] => this.postService.myPosts.getValue();

  fetchPosts(): void {
    this.postService.getMyPosts();
  }
}
