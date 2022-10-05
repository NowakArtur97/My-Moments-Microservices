import {
  AfterViewInit,
  Component,
  ElementRef,
  OnInit,
  ViewChild,
} from '@angular/core';

import { ClickAndDragToScrollService } from '../../common/services/click-and-drag-to-scroll.service';
import Post from '../models/post.model';
import { PostService } from '../services/post.service';

@Component({
  selector: 'app-posts',
  templateUrl: './posts.component.html',
  styleUrls: ['./posts.component.css'],
})
export class PostsComponent implements OnInit, AfterViewInit {
  @ViewChild('postsContainer') postsContainer:
    | ElementRef<HTMLDivElement>
    | undefined;

  posts: Post[] = [];

  constructor(
    private postService: PostService,
    private clickAndDragToScrollService: ClickAndDragToScrollService
  ) {}

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

  ngAfterViewInit(): void {
    this.clickAndDragToScrollService.scrolledElement = this.postsContainer;
  }

  startScroll = (event: MouseEvent): void =>
    this.clickAndDragToScrollService.startScroll(event);

  stopScroll = (): void => this.clickAndDragToScrollService.stopScroll();

  dragAndScroll = (event: MouseEvent): void =>
    this.clickAndDragToScrollService.dragAndScroll(event);

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
