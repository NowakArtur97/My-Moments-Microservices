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
    trigger('state', [
      state(
        'inactive',
        style({
          transform: 'rotateY(0deg) scale(0.6)',
        })
      ),
      state(
        'active',
        style({
          transform: 'rotateY(0deg) scale(1)',
        })
      ),
      state(
        'comments',
        style({
          transform: 'rotateY(180deg) scale(1)',
        })
      ),
      transition('inactive => active', animate('0.5s')),
      transition('active => inactive', animate('0.5s')),
      transition('active => comments', animate('1.5s')),
      transition('comments => active', animate('1.5s')),
      transition('comments => inactive', animate('1.5s')),
    ]),
  ],
})
export class PostComponent implements OnInit, OnChanges, AfterViewChecked {
  @Input() post!: PostElement;
  @ViewChild('postElement') postElement!: ElementRef<HTMLDivElement>;
  @ViewChild('postData') postData!: ElementRef<HTMLDivElement>;

  areCommentsVisible = false;
  isRotating = false;

  startHeight!: number;
  startWidth!: number;

  private stateTimeout!: NodeJS.Timeout;
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
      this.changeCommentsVisibilityOnTimeout(false);
    }
  }

  ngOnChanges(): void {
    this.setupStyles();
  }

  onShowComments(): void {
    if (this.post.state === PostState.INACTIVE || this.isRotating) {
      return;
    }
    clearTimeout(this.stateTimeout);
    if (this.post.state === PostState.ACTIVE) {
      clearTimeout(this.rotationTimeout);
      const boundingClientRect = this.postElement.nativeElement.getBoundingClientRect();
      this.startHeight = boundingClientRect.height;
      this.startWidth = boundingClientRect.width;
      this.post.state = PostState.COMMENTS_SHOWEN;
      this.isRotating = true;
      this.commentService.getComments(this.post.id);
      this.changeCommentsVisibilityOnTimeout(true);
      this.rotationTimeout = setTimeout(() => {
        this.isRotating = false;
      }, 1500);
    } else {
      this.post.state = PostState.ACTIVE;
      this.changeCommentsVisibilityOnTimeout(false);
    }
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

  getStateForAnimation(): string {
    switch (this.post.state) {
      case PostState.ACTIVE:
        return 'active';
      case PostState.INACTIVE:
        return 'inactive';
      case PostState.COMMENTS_SHOWEN:
        return 'comments';
    }
  }

  isActive = (): boolean => this.post.state === PostState.ACTIVE;

  shouldShowComments = (): boolean =>
    this.post.state === PostState.COMMENTS_SHOWEN;

  areChangeCurrentPhotoButtonsVisible(): boolean {
    return this.post.state === PostState.ACTIVE;
  }

  private setupStyles(): void {
    if (this.postElement === undefined) {
      return;
    }
    if (this.post.isCurrentlyLastElement) {
      this.fixPaddingOfLastElement();
    }
  }

  private fixPaddingOfLastElement(): void {
    if (this.postData === undefined) {
      return;
    }
    // TODO: Fix issue with width (padding)
    const lastPost: HTMLDivElement = this.postData.nativeElement;
    this.renderer.setStyle(lastPost, 'padding-right', '22vw');
    const changeButtonsWrapperChildren =
      lastPost.children[lastPost.children.length - 1].children;
    if (changeButtonsWrapperChildren.length === 0) {
      return;
    }
    const leftChangePhotoButton = changeButtonsWrapperChildren[0];
    if (leftChangePhotoButton) {
      this.renderer.setStyle(leftChangePhotoButton, 'left', 'calc(2% + 11vw)');
    }
    const rightChangePhotoButton = changeButtonsWrapperChildren[1];
    if (rightChangePhotoButton) {
      this.renderer.setStyle(
        rightChangePhotoButton,
        'right',
        'calc(2% + 11vw)'
      );
    }
  }

  private changeCommentsVisibilityOnTimeout(
    areCommentsVisibleAfterTimeout: boolean
  ): void {
    this.stateTimeout = setTimeout(() => {
      this.areCommentsVisible = areCommentsVisibleAfterTimeout;
    }, 750);
  }
}
