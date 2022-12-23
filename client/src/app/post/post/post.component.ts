import { animate, state, style, transition, trigger } from '@angular/animations';
import { AfterViewChecked, Component, ElementRef, Input, OnChanges, OnInit, Renderer2, ViewChild } from '@angular/core';
import { CommentService } from 'src/app/comments/services/comments.service';

import PostElement from '../models/post-element.model';
import PostState from '../models/post-state.enum';

@Component({
  selector: 'app-post',
  templateUrl: './post.component.html',
  styleUrls: ['./post.component.css'],
  animations: [
    trigger('rotate', [
      state(
        'post',
        style({
          transform: 'rotateY(0deg)',
          // transform: 'rotateY(0deg) scale(1.2)',
        })
      ),
      state(
        'comments',
        style({
          transform: 'rotateY(180deg)',
          // transform: 'rotateY(180deg) scal e(1.2)',
        })
      ),
      transition('post => comments', animate('2s')),
      transition('comments => post', animate('2s')),
    ]),
  ],
})
export class PostComponent implements OnInit, OnChanges, AfterViewChecked {
  @Input() post!: PostElement;
  @ViewChild('postElement') postElement!: ElementRef<HTMLDivElement>;
  @ViewChild('postData') postData!: ElementRef<HTMLDivElement>;

  areCommentsVisible = false;

  startHeight!: number;
  startWidth!: number;

  private readonly POST_TRANSFORM_SCALE = {
    active: 1.2,
    inactive: 0.6,
  };
  private rotationTimeout!: NodeJS.Timeout;

  constructor(
    private commentService: CommentService,
    private renderer: Renderer2
  ) {}

  ngOnInit(): void {}

  ngAfterViewChecked(): void {
    this.setupStyles();
    if (this.post.stoppedBeingActive) {
      this.post.stoppedBeingActive = false;
      clearTimeout(this.rotationTimeout);
      this.rotationTimeout = setTimeout(() => {
        this.areCommentsVisible = false;
      }, 1000);
    }
  }

  ngOnChanges(): void {
    this.setupStyles();
  }

  onShowComments(): void {
    if (this.post.state === PostState.INACTIVE) {
      return;
    }
    clearTimeout(this.rotationTimeout);
    if (this.post.state === PostState.ACTIVE) {
      const boundingClientRect = this.postElement.nativeElement.getBoundingClientRect();
      this.startHeight = boundingClientRect.height;
      this.startWidth = boundingClientRect.width;
      this.commentService.getComments(this.post.id);
      this.post.state = PostState.COMMENTS_SHOWEN;
      this.rotationTimeout = setTimeout(() => {
        this.areCommentsVisible = true;
      }, 1000);
    } else {
      this.post.state = PostState.ACTIVE;
      this.rotationTimeout = setTimeout(() => {
        this.areCommentsVisible = false;
      }, 1000);
    }
    // this.setupStyles();
  }

  onChangeCurrentPhoto(direction: number): void {
    const post = this.post;
    const currentPhotoIndex = post.currentPhotoIndex;
    if (direction === 1 && currentPhotoIndex < post.photos.length - 1) {
      post.currentPhotoIndex = currentPhotoIndex + 1;
    } else if (direction === -1 && currentPhotoIndex > 0) {
      post.currentPhotoIndex = currentPhotoIndex - 1;
    }
  }

  shouldShowComments = (): boolean =>
    this.post.state === PostState.COMMENTS_SHOWEN;

  private setupStyles(): void {
    if (this.postElement === undefined) {
      return;
    }
    if (this.post.isCurrentlyLastElement) {
      this.fixPaddingOfLastElement();
    }
    if (
      this.post.state === PostState.ACTIVE ||
      this.post.state === PostState.COMMENTS_SHOWEN
    ) {
      this.setTransformScale(this.POST_TRANSFORM_SCALE.active);
    } else if (this.post.state === PostState.INACTIVE) {
      this.setTransformScale(this.POST_TRANSFORM_SCALE.inactive);
    }
  }

  // TODO: Fix transformations
  private setTransformScale = (scale: number): void =>
    this.renderer.setStyle(
      this.postElement.nativeElement,
      'transformXXX',
      `scale(${scale})`
    );

  private fixPaddingOfLastElement(): void {
    if (this.postData === undefined) {
      return;
    }
    // TODO: Fix issue with width (padding)
    const lastPost: HTMLDivElement = this.postData.nativeElement;
    this.renderer.setStyle(lastPost, 'padding-right', '22vw');
    const changeButtonsWrapperChildren =
      lastPost.children[lastPost.children.length - 1].children;
    const leftChangePhotoButton = changeButtonsWrapperChildren[0];
    this.renderer.setStyle(leftChangePhotoButton, 'left', 'calc(2% + 11vw)');
    const rightChangePhotoButton = changeButtonsWrapperChildren[1];
    this.renderer.setStyle(rightChangePhotoButton, 'right', 'calc(2% + 11vw)');
  }
}
