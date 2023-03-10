import { animate, state, style, transition, trigger } from '@angular/animations';
import { Component, Input, OnInit } from '@angular/core';

import Comment from '../models/comment.model';
import { CommentService } from '../services/comments.service';

@Component({
  selector: 'app-comment',
  templateUrl: './comment.component.html',
  styleUrls: ['./comment.component.css'],
  animations: [
    trigger('hideText', [
      state(
        'leave',
        style({
          color: 'hsl(0, 0%, 0%)',
        })
      ),
      state(
        'enter',
        style({
          color: 'hsl(0, 0%, 100%)',
        })
      ),
      transition('leave => enter', [animate('0.5s')]),
      transition('enter => leave', [animate('0.5s 0.5s')]),
    ]),
  ],
})
export class CommentComponent implements OnInit {
  @Input() postId!: string;
  @Input() comment!: Comment;
  private readonly DELETE_STATE = {
    ENTER: 'enter',
    LEAVE: 'leave',
  };
  deleteState: string = '';

  constructor(private commentService: CommentService) {}

  ngOnInit(): void {}

  getFormatedDate = (): string =>
    // DD/MM/YYYY
    new Date(this.comment.createDate).toLocaleDateString('pt-PT');

  onDeleteComment(): void {
    this.commentService.deleteComment(this.postId, this.comment.id);
  }

  onHoverDeleteButton(isHovered: boolean): void {
    this.deleteState = isHovered
      ? this.DELETE_STATE.ENTER
      : this.DELETE_STATE.LEAVE;
  }
}
