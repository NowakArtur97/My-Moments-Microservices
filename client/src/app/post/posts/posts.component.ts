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
      (posts) => {
        this.posts = this.postService.mapBinaryToJpgs(posts);
        this.posts = this.posts.map((post) => ({
          ...post,
          photos: this.shuffleArray(post.photos),
        }));
      } // TODO: PostsComponent: Remove
    );
  }

  // TODO: PostsComponent: Remove
  private shuffleArray(array: any[]): any[] {
    for (var i = array.length - 1; i > 0; i--) {
      var j = Math.floor(Math.random() * (i + 1));
      var temp = array[i];
      array[i] = array[j];
      array[j] = temp;
    }
    return array;
  }
}
