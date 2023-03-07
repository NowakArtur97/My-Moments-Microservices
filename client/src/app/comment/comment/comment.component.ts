import { animate, keyframes, state, style, transition, trigger } from '@angular/animations';
import { Component, Input, OnInit } from '@angular/core';

import Comment from '../models/comment.model';
import { CommentService } from '../services/comments.service';

@Component({
  selector: 'app-comment',
  templateUrl: './comment.component.html',
  styleUrls: ['./comment.component.css'],
  animations: [
    trigger('onHoverDelete', [
      state(
        'enter',
        style({
          color: 'hsl(0, 0%, 100%)',
        })
      ),
      state(
        'leave',
        style({
          color: 'hsl(0, 0%, 0%)',
        })
      ),
      transition('enter <=> leave', [animate('1s')]),
    ]),
    trigger('onHoverDelete2', [
      state(
        'enter',
        style({
          // display: 'block',
          background: 'hsl(0, 0%, 0%)',
          transform: 'scale(1.5) rotate(60deg) translateY(-70%)',
        })
      ),
      state(
        'leave',
        style({
          // display: 'none',
          background: 'hsl(0, 0%, 100%)',
          transform: 'translateY(-50%)',
        })
      ),
      transition('enter => leave', [
        animate(
          '1s',
          keyframes([
            style({
              background: 'hsl(0, 0%, 0%)',
              transform: 'scale(1.5) rotate(60deg) translateY(-70%)',
              offset: 0,
            }),
            style({
              background: 'hsl(0, 0%, 0%)',
              transform: 'translateY(-50%)',
              offset: 0.2,
            }),
            style({
              background: 'hsl(0, 0%, 100%)',
              transform: 'translateY(-50%)',
              offset: 1,
            }),
          ])
        ),
      ]),
      transition('leave => enter', [
        animate(
          '1s',
          keyframes([
            style({
              background: 'hsl(0, 0%, 100%)',
              transform: 'translateY(-50%)',
              offset: 0,
            }),
            style({
              background: 'hsl(0, 0%, 0%)',
              transform: 'translateY(-50%)',
              offset: 0.2,
            }),
            style({
              background: 'hsl(0, 0%, 0%)',
              transform: 'scale(1.5) rotate(60deg) translateY(-70%)',
              offset: 1,
            }),
          ])
        ),
      ]),
    ]),
    // trigger('onHoverDelete3', [
    //   query(
    //     '.comment__bin::before',
    //     state(
    //       'enter',
    //       style({
    //         transform: 'rotate(-90deg) translateX(50%) translateY(-5%)',
    //       })
    //     ),
    //     state(
    //       'leave',
    //       style({
    //         transform: 'translateY(0%)',
    //       })
    //     )
    //   ),
    //   transition('enter <=> leave', [animate('1s 1s')]),
    // ]),
  ],
})
export class CommentComponent implements OnInit {
  @Input() postId!: string;
  @Input() comment!: Comment;
  private readonly DELETE_STATE = {
    ENTER: 'enter',
    LEAVE: 'leave',
  };
  deleteState: string = this.DELETE_STATE.LEAVE;

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
