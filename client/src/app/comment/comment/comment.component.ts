import { animate, keyframes, state, style, transition, trigger } from '@angular/animations';
import { Component, Input, OnInit } from '@angular/core';

import Comment from '../models/comment.model';
import { CommentService } from '../services/comments.service';

@Component({
  selector: 'app-comment',
  templateUrl: './comment.component.html',
  styleUrls: ['./comment.component.css'],
  animations: [
    trigger('hide', [
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
    trigger('bin', [
      state(
        'leave',
        style({
          transform: 'translate(0%, -50%) scale(0)',
        })
      ),
      state(
        'enter',
        style({
          transform: 'rotate(60deg) translate(-30%, -70%) scale(1.5)',
        })
      ),
      transition('leave => enter', [
        animate(
          '0.5s',
          keyframes([
            style({
              transform: 'translate(0%, -50%) scale(0)',
              offset: 0,
            }),
            style({
              transform: 'translate(0%, -50%) scale(1.5)',
              offset: 0.2,
            }),
            style({
              transform: 'rotate(60deg) translate(-30%, -70%) scale(1.5)',
              offset: 1,
            }),
          ])
        ),
      ]),
      transition('enter => leave', [
        animate(
          '0.5s',
          keyframes([
            style({
              transform: 'rotate(60deg) translate(-30%, -70%) scale(1.5)',
              offset: 0,
            }),
            style({
              transform: 'rotate(0deg) translate(0%, -50%) scale(1.5)',
              offset: 0.8,
            }),
            style({
              transform: 'translate(0%, -50%) scale(0)',
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
    //   transition('enter <=> leave', [animate('0.5s 0.5s')]),
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
