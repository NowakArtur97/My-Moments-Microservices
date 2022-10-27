import {
  AfterViewChecked,
  AfterViewInit,
  Component,
  ElementRef,
  OnInit,
  QueryList,
  Renderer2,
  ViewChild,
  ViewChildren,
} from '@angular/core';

import { ClickAndDragToScrollService } from '../../common/services/click-and-drag-to-scroll.service';
import Post from '../models/post.model';
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

  private readonly POST_TRANSFORM_SCALE = {
    active: 1.2,
    inactive: 0.6,
  };
  private areLastPostStylesFixed = false;

  posts: Post[] = [];

  constructor(
    private postService: PostService,
    private clickAndDragToScrollService: ClickAndDragToScrollService,
    private renderer: Renderer2
  ) {}

  ngOnInit(): void {
    this.postService.myPosts.subscribe((posts) => (this.posts = posts));
  }

  ngAfterViewInit(): void {
    this.clickAndDragToScrollService.scrolledElement = this.postsContainer;
    this.setActivePost(0);
  }

  ngAfterViewChecked(): void {
    if (!this.areLastPostStylesFixed && this.postElements.length > 1) {
      this.areLastPostStylesFixed = true;
      this.setActivePost(0);
      this.fixPaddingOfLastElement();
    }
  }

  onStartScroll = (event: MouseEvent): void =>
    this.clickAndDragToScrollService.startScroll(event);

  onStopScroll = (): void => this.clickAndDragToScrollService.stopScroll();

  onDragAndScroll(event: MouseEvent): void {
    this.clickAndDragToScrollService.dragAndScroll(event);
    if (!this.clickAndDragToScrollService.isScrolling) {
      return;
    }
    const center =
      this.centerMarker.nativeElement.getBoundingClientRect().x +
      this.postsContainer.nativeElement.scrollLeft;
    this.setActivePost(center);
  }

  setActivePost(
    center: number = this.centerMarker.nativeElement.getBoundingClientRect().x +
      this.postsContainer.nativeElement.scrollLeft
  ): void {
    if (this.posts.length === 0) {
      return;
    }
    const activeElement = this.postElements
      .map((element) => element as ElementRef)
      .reduce((previousElement, currentElement) => {
        const currentOffsetLeft = currentElement?.nativeElement.offsetLeft;
        const previousOffsetLeft = previousElement?.nativeElement.offsetLeft;
        return Math.abs(currentOffsetLeft - center) <
          Math.abs(previousOffsetLeft - center)
          ? currentElement
          : previousElement;
      });
    this.postElements
      .filter((element) => element != activeElement)
      .map((element) => {
        this.setTransformScale(element, this.POST_TRANSFORM_SCALE.inactive);
        return element as ElementRef;
      });
    this.setTransformScale(activeElement, this.POST_TRANSFORM_SCALE.active);
  }

  onChangeCurrentPhoto(direction: number, postIndex: number): void {
    const post = this.posts[postIndex];
    const currentPhotoIndex = post.currentPhotoIndex;
    if (direction == 1 && currentPhotoIndex < post.photos.length - 1) {
      post.currentPhotoIndex = currentPhotoIndex + 1;
    } else if (direction == -1 && currentPhotoIndex > 0) {
      post.currentPhotoIndex = currentPhotoIndex - 1;
    }
  }

  private setTransformScale = (element: ElementRef<any>, scale: number): void =>
    this.renderer.setStyle(
      element.nativeElement,
      'transform',
      `scale(${scale})`
    );

  private fixPaddingOfLastElement(): void {
    const lastPost: HTMLDivElement = this.postElements.last.nativeElement;
    this.renderer.setStyle(lastPost, 'padding-right', '22vw');
    const changeButtonsWrapperChildren =
      lastPost.children[lastPost.children.length - 1].children;
    const leftChangePhotoButton = changeButtonsWrapperChildren[0];
    this.renderer.setStyle(leftChangePhotoButton, 'left', 'calc(2% + 11vw)');
    const rightChangePhotoButton = changeButtonsWrapperChildren[1];
    this.renderer.setStyle(rightChangePhotoButton, 'right', 'calc(2% + 11vw)');
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
