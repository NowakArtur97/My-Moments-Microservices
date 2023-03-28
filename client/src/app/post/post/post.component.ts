import { animate, state, style, transition, trigger } from '@angular/animations';
import { AfterViewChecked, Component, ElementRef, Input, OnChanges, OnInit, Renderer2, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { CommentService } from 'src/app/comment/services/comments.service';
import { APP_ROUTES } from 'src/app/common/const.data';

import PostElement from '../models/post-element.model';
import PostState from '../models/post-state.enum';
import { PostService } from '../services/post.service';

@Component({
  selector: 'app-post',
  templateUrl: './post.component.html',
  styleUrls: ['./post.component.css'],
  animations: [
    trigger('postState', [
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
    trigger('editPost', [
      state(
        'enter',
        style({
          transform: 'translateY(0) scale(1)',
        })
      ),
      state(
        'edit',
        style({
          transform: 'translateY(-200%) scale(1)',
        })
      ),
      transition('enter => edit', [animate('0.5s')]),
    ]),
    trigger('deletePost', [
      state(
        'enter',
        style({
          transform: 'translateY(0) scale(1)',
        })
      ),
      state(
        'delete',
        style({
          transform: 'translateY(200%) scale(1)',
        })
      ),
      transition('enter => delete', [animate('0.5s')]),
    ]),
  ],
})
export class PostComponent implements OnInit, OnChanges, AfterViewChecked {
  @Input() post!: PostElement;
  @ViewChild('postElement') postElement!: ElementRef<HTMLDivElement>;

  private readonly RIGHT_PADDING_FIX = 27;
  private readonly FULL_ANIMATION_TIME = 1500;
  private readonly HALF_ANIMATION_TIME = this.FULL_ANIMATION_TIME / 2;

  private readonly EDIT_STATE = {
    ENTER: 'enter',
    EDIT: 'edit',
  };
  private readonly DELETE_STATE = {
    ENTER: 'enter',
    DELETE: 'delete',
  };
  editState = this.DELETE_STATE.ENTER;
  deleteState = this.DELETE_STATE.ENTER;
  areCommentsVisible = false;
  isRotating = false;

  startHeight!: number;
  startWidth!: number;
  showCommentsAnimationStartTime!: number;

  private stateTimeout!: NodeJS.Timeout;
  private rotationTimeout!: NodeJS.Timeout;

  constructor(
    private postService: PostService,
    private commentService: CommentService,
    private renderer: Renderer2,
    private router: Router
  ) {}

  ngOnInit(): void {}

  ngAfterViewChecked(): void {
    this.setupStyles();
    if (this.post.stoppedBeingActive) {
      this.post.stoppedBeingActive = false;
      clearTimeout(this.rotationTimeout);
      this.commentService.stopEditingComment();
      this.changeCommentsVisibilityOnTimeout(false);
    }
  }

  ngOnChanges(): void {
    this.setupStyles();
  }

  onEditPost(): void {
    if (this.deleteState === this.DELETE_STATE.DELETE) {
      return;
    }
    this.editState = this.EDIT_STATE.EDIT;
    this.postService.startEditingPost(this.post);
  }

  onPostEditAnimationFinished(): void {
    if (this.editState === this.EDIT_STATE.EDIT) {
      this.router.navigate([`/${APP_ROUTES.post.editor}`]);
    }
  }

  onDeletePost(): void {
    if (this.editState === this.EDIT_STATE.EDIT) {
      return;
    }
    this.deleteState = this.DELETE_STATE.DELETE;
    this.postService.deletePost(this.post.id);
  }

  onPostDeleteAnimationFinished(): void {
    if (this.deleteState === this.DELETE_STATE.DELETE) {
      this.postService.hidePost(this.post.id);
    }
  }

  onShowComments(): void {
    if (this.post.state === PostState.INACTIVE || this.isRotating) {
      return;
    }
    clearTimeout(this.stateTimeout);
    clearTimeout(this.rotationTimeout);
    this.isRotating = true;
    if (this.post.state === PostState.ACTIVE) {
      const boundingClientRect = this.postElement.nativeElement.getBoundingClientRect();
      this.setSizes(boundingClientRect);
      this.post.state = PostState.COMMENTS_SHOWEN;
      this.showCommentsAnimationStartTime = new Date().getTime();
      this.commentService.getComments(this.post.id);
      this.changeCommentsVisibilityOnTimeout(true);
    } else {
      this.post.state = PostState.ACTIVE;
      this.commentService.stopEditingComment();
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
    if (
      this.editState === this.EDIT_STATE.EDIT ||
      this.deleteState === this.DELETE_STATE.DELETE
    ) {
      return 'default';
    }
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

  areFrontElementsVisible(): boolean {
    return this.post.state === PostState.ACTIVE && !this.isRotating;
  }

  private setupStyles(): void {
    if (this.post.isCurrentlyLastElement && !this.isRotating) {
      this.fixPaddingOfLastElement();
    }
  }

  private fixPaddingOfLastElement(): void {
    if (this.postElement === undefined) {
      return;
    }
    const lastPost: HTMLDivElement = this.postElement.nativeElement;
    this.renderer.setStyle(
      lastPost,
      'padding-right',
      `${this.RIGHT_PADDING_FIX}vw`
    );
  }

  private changeCommentsVisibilityOnTimeout(
    areCommentsVisibleAfterTimeout: boolean
  ): void {
    let timeout;
    const timeDifference =
      new Date().getTime() - this.showCommentsAnimationStartTime;
    const isInFirstHalfOfRotation =
      !areCommentsVisibleAfterTimeout &&
      timeDifference < this.HALF_ANIMATION_TIME;
    const isInSecondHalfOfRotation =
      !areCommentsVisibleAfterTimeout &&
      timeDifference > this.HALF_ANIMATION_TIME &&
      timeDifference < this.FULL_ANIMATION_TIME;
    if (isInFirstHalfOfRotation) {
      clearTimeout(this.stateTimeout);
      timeout = timeDifference - this.HALF_ANIMATION_TIME;
      this.rotationTimeout = setTimeout(() => {
        this.isRotating = false;
      }, timeout);
      this.areCommentsVisible = areCommentsVisibleAfterTimeout;
      return;
    } else if (isInSecondHalfOfRotation) {
      timeout = timeDifference - this.HALF_ANIMATION_TIME;
      this.rotationTimeout = setTimeout(() => {
        this.isRotating = false;
      }, timeout);
    } else {
      timeout = this.HALF_ANIMATION_TIME;
      this.rotationTimeout = setTimeout(() => {
        this.isRotating = false;
      }, this.FULL_ANIMATION_TIME);
    }
    this.stateTimeout = setTimeout(() => {
      this.areCommentsVisible = areCommentsVisibleAfterTimeout;
    }, timeout);
  }

  private setSizes(boundingClientRect: DOMRect) {
    if (this.post.isCurrentlyLastElement) {
      this.startWidth =
        boundingClientRect.width -
        window.innerWidth * +`0.${this.RIGHT_PADDING_FIX}`;
    } else {
      this.startWidth = boundingClientRect.width;
    }
    this.startHeight = boundingClientRect.height;
  }
}
