import { Component, OnInit } from '@angular/core';

import Post from '../models/post.model';
import { PostService } from '../services/post.service';

@Component({
  selector: 'app-posts',
  templateUrl: './posts.component.html',
  styleUrls: ['./posts.component.css'],
})
export class PostsComponent implements OnInit {
  posts: Post[] = [];

  constructor(private postService: PostService) {}

  ngOnInit(): void {
    this.postService.myPosts.subscribe(
      (posts) => (this.posts = this.postService.mapBinaryToJpgs(posts)) // TODO: PostsComponent: Remove
    );
  }
}
