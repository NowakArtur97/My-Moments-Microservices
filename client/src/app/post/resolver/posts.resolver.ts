import { Injectable } from '@angular/core';
import { Resolve } from '@angular/router';

import Post from '../models/post.model';
import { PostService } from '../services/post.service';

@Injectable({ providedIn: 'root' })
export default abstract class PostsResolver implements Resolve<any> {
  constructor(protected postService: PostService) {}

  public resolve(): Post[] {
    const posts = this.getPosts();
    if (posts.length === 0) {
      this.fetchPosts();
      return this.getPosts();
    } else {
      return posts;
    }
  }

  abstract getPosts(): Post[];

  abstract fetchPosts(): void;
}
