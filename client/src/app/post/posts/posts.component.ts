import { AfterViewInit, Component, ElementRef, OnInit, QueryList, ViewChild, ViewChildren } from '@angular/core';

import { ClickAndDragToScrollService } from '../../common/services/click-and-drag-to-scroll.service';
import PostElement from '../models/post-element.model';
import { PostService } from '../services/post.service';

@Component({
  selector: 'app-posts',
  templateUrl: './posts.component.html',
  styleUrls: ['./posts.component.css'],
})
export class PostsComponent implements OnInit, AfterViewInit {
  @ViewChild('postsContainer') postsContainer!: ElementRef<HTMLDivElement>;
  @ViewChildren('postsElements') postsElements!: QueryList<HTMLDivElement>;

  posts: PostElement[] = [];

  constructor(
    private postService: PostService,
    private clickAndDragToScrollService: ClickAndDragToScrollService
  ) {}

  ngOnInit(): void {
    this.postService.myPosts.subscribe((posts) => {
      this.posts = this.postService
        .mapBinaryToJpgs(posts) // TODO: PostsComponent: Remove
        .map((post) => ({
          ...post,
          photos: this.shuffleArray(post.photos),
          isActive: false,
          offsetLeft: 0,
        }));
    });
  }

  ngAfterViewInit(): void {
    this.clickAndDragToScrollService.scrolledElement = this.postsContainer;
  }

  startScroll = (event: MouseEvent): void =>
    this.clickAndDragToScrollService.startScroll(event);

  stopScroll = (): void => this.clickAndDragToScrollService.stopScroll();

  dragAndScroll(event: MouseEvent): void {
    this.clickAndDragToScrollService.dragAndScroll(event);
    if (!this.clickAndDragToScrollService.isScrolling) {
      return;
    }
    const goal = this.postsContainer.nativeElement.scrollLeft;
    console.log(goal);
    this.posts = this.posts.map((post, index) => {
      const offsetLeft =
        window.scrollX +
        (this.postsElements.get(index) as HTMLDivElement).offsetLeft;
      return { ...post, isActive: false, offsetLeft };
    });
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
