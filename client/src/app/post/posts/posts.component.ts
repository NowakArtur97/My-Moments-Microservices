import {
  AfterViewChecked,
  AfterViewInit,
  Component,
  ElementRef,
  OnInit,
  QueryList,
  ViewChild,
  ViewChildren,
} from '@angular/core';

import { ClickAndDragToScrollService } from '../../common/services/click-and-drag-to-scroll.service';
import PostElement from '../models/post-element.model';
import PostState from '../models/post-state.enum';
import { PostService } from '../services/post.service';

@Component({
  selector: 'app-posts',
  templateUrl: './posts.component.html',
  styleUrls: ['./posts.component.css'],
})
export class PostsComponent implements OnInit, AfterViewInit, AfterViewChecked {
  @ViewChild('centerMarker') centerMarker!: ElementRef<HTMLDivElement>;
  @ViewChild('postsContainer') postsContainer!: ElementRef<HTMLDivElement>;
  @ViewChildren('postsElements') postElements!: QueryList<ElementRef>;

  posts: PostElement[] = [];
  previousActiveElement!: PostElement;

  constructor(
    private postService: PostService,
    private clickAndDragToScrollService: ClickAndDragToScrollService
  ) {}

  // TODO: PostsComponent: Remove
  ngOnInit(): void {
    this.postService.myPosts.subscribe((posts) => {
      const postsWithMappedImages = this.postService
        .mapBinaryToJpgs(posts)
        .map((post) => ({
          ...post,
          photos: this.shuffleArray(post.photos),
        }));
      this.posts = this.postService.mapPostsToElements(postsWithMappedImages);
      if (this.posts.length > 1) {
        this.posts[this.posts.length - 1].isCurrentlyLastElement = true;
      }
    });
  }

  // ngOnInit(): void {
  //   this.postService.myPosts.subscribe((posts) => {
  //     this.posts = posts.map((post) => ({
  //       ...post,
  //       currentPhotoIndex: 0,
  //       isActive: false,
  //       isCurrentlyLastElement: false,
  //     }));
  //     this.posts[this.posts.length - 1].isCurrentlyLastElement = true;
  //   });
  // }

  ngAfterViewInit(): void {
    this.clickAndDragToScrollService.scrolledElement = this.postsContainer;
    this.setActivePost(0);
  }

  // TODO: PostsComponent: Remove?
  ngAfterViewChecked(): void {
    // if (this.postElements.length > 1) {
    //   this.previousActiveElement = this.posts[this.postElements.length - 1];
    // }
  }

  onStartScroll = (event: MouseEvent): void =>
    this.clickAndDragToScrollService.startScroll(event);

  onStopScroll = (): void => this.clickAndDragToScrollService.stopScroll();

  onDragAndScroll(event: MouseEvent): void {
    this.clickAndDragToScrollService.dragAndScroll(event);
    if (!this.clickAndDragToScrollService.isScrolling) {
      return;
    }
    this.setActivePost();
  }

  setActivePost(center: number = this.calculateCenterPositionOfPosts()): void {
    if (this.posts.length === 0) {
      return;
    }
    const activeElement = this.choseActivePost(center);
    if (this.previousActiveElement !== activeElement) {
      this.posts.forEach((post) => (post.state = PostState.INACTIVE));
      this.previousActiveElement = activeElement;
      activeElement.state = PostState.ACTIVE;
    }
  }

  private choseActivePost(
    center: number = this.calculateCenterPositionOfPosts()
  ): PostElement {
    const postElement = this.postElements
      .map((element) => element as ElementRef)
      .reduce((previousElement, currentElement) => {
        const currentOffsetLeft = currentElement?.nativeElement.offsetLeft;
        const previousOffsetLeft = previousElement?.nativeElement.offsetLeft;
        return Math.abs(currentOffsetLeft - center) <
          Math.abs(previousOffsetLeft - center)
          ? currentElement
          : previousElement;
      });
    let activeElementIndex = 0;
    this.postElements.forEach((element, index) => {
      if (element === postElement) {
        activeElementIndex = index;
      }
    });
    return this.posts[activeElementIndex];
  }

  private calculateCenterPositionOfPosts = (): number =>
    this.centerMarker.nativeElement.getBoundingClientRect().x +
    this.postsContainer.nativeElement.scrollLeft;

  // TODO: PostsComponent: Remove
  private shuffleArray(array: any[]): any[] {
    for (let i = array.length - 1; i > 0; i--) {
      const j = Math.floor(Math.random() * (i + 1));
      const temp = array[i];
      array[i] = array[j];
      array[j] = temp;
    }
    return array;
  }
}
