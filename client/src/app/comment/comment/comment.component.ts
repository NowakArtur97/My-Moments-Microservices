import { animate, state, style, transition, trigger } from '@angular/animations';
import { Component, ElementRef, Input, OnInit, ViewChild } from '@angular/core';

import Comment from '../models/comment.model';
import { CommentService } from '../services/comments.service';

@Component({
  selector: 'app-comment',
  templateUrl: './comment.component.html',
  styleUrls: ['./comment.component.css'],
  animations: [
    trigger('deleteComment', [
      state(
        'enter',
        style({
          height: '12vh',
        })
      ),
      state(
        'delete',
        style({
          height: '0',
        })
      ),
      transition('enter => delete', [animate('0.5s')]),
    ]),
  ],
})
export class CommentComponent implements OnInit {
  @Input() postId!: string;
  @Input() comment!: Comment;

  private readonly DELETE_STATE = {
    ENTER: 'enter',
    LEAVE: 'leave',
    DELETE: 'delete',
  };
  @ViewChild('commentContentWrapper')
  commentContentWrapper!: ElementRef<HTMLDivElement>;
  deleteState: string = '';
  private wasEditingStarted: boolean = false;

  constructor(private commentService: CommentService) {}

  ngOnInit(): void {}

  onStartEditingComment(): void {
    this.wasEditingStarted = true;
  }

  onHoverEditButton(isHovered: boolean): void {
    if (!this.wasEditingStarted) {
      if (isHovered) {
        this.commentService.startEditingComment(this.comment);
      } else {
        this.commentService.stopEditingComment();
      }
    }
  }

  onDeleteComment(): void {
    this.deleteState = this.DELETE_STATE.DELETE;
    if (this.wasEditingStarted) {
      this.wasEditingStarted = false;
      this.commentService.stopEditingComment();
    }
    this.commentService.deleteComment(this.postId, this.comment.id);
  }

  onHoverDeleteButton(isHovered: boolean): void {
    if (this.deleteState === this.DELETE_STATE.DELETE) {
      return;
    }
    if (isHovered) {
      this.commentContentWrapper.nativeElement.scrollTop = 0;
    }
    if (this.wasEditingStarted) {
      this.wasEditingStarted = false;
      this.commentService.stopEditingComment();
    }
    this.deleteState = isHovered
      ? this.DELETE_STATE.ENTER
      : this.DELETE_STATE.LEAVE;
  }

  onCommentDeleteAnimationFinished(): void {
    if (this.deleteState === this.DELETE_STATE.DELETE) {
      this.commentService.hideComment(this.comment.id);
    }
  }

  getFormatedDate = (): string =>
    // DD/MM/YYYY
    new Date(this.comment.createDate).toLocaleDateString('pt-PT');
}
