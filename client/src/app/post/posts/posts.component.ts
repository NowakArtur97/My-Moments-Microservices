import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';

import Post from '../models/post.model';
import { PostService } from '../services/post.service';

@Component({
  selector: 'app-posts',
  templateUrl: './posts.component.html',
  styleUrls: ['./posts.component.css'],
})
export class PostsComponent implements OnInit {
  @ViewChild('postsContainer') postsContainer:
    | ElementRef<HTMLDivElement>
    | undefined;

  posts: Post[] = [];
  private isScrolling = false;
  private startXPosition = 0;
  private scrollLeftPosition = 0;

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

  startScroll(e: MouseEvent): void {
    this.isScrolling = true;
    const { offsetLeft, scrollLeft } = this.postsContainer!!.nativeElement;
    this.startXPosition = e.pageX - offsetLeft;
    this.scrollLeftPosition = scrollLeft;
  }

  stopScroll(): void {
    this.isScrolling = false;
  }

  dragAndScroll(e: MouseEvent): void {
    e.preventDefault();
    if (!this.isScrolling) {
      return;
    }
    const postsContainer = this.postsContainer!!.nativeElement;
    const xPosition = e.pageX - postsContainer.offsetLeft;
    const walk = xPosition - this.startXPosition;
    postsContainer.scrollLeft = this.scrollLeftPosition - walk;
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
