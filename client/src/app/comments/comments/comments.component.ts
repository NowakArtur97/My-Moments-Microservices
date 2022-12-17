import { Component, Input, OnChanges, OnInit } from '@angular/core';

import Comment from '../models/comment.model';
import { CommentService } from '../services/comments.service';

@Component({
  selector: 'app-comments',
  templateUrl: './comments.component.html',
  styleUrls: ['./comments.component.css'],
})
export class CommentsComponent implements OnInit, OnChanges {
  @Input() id!: string;
  comments: Comment[] = [];

  constructor(private commentService: CommentService) {}

  // TODO: subscribe/unsubscribe onDestroy?
  ngOnInit(): void {
    this.commentService.comments.subscribe((comments) => {
      console.log(comments);
      this.comments = comments;
    });
  }

  ngOnChanges(): void {
    if (this.id === undefined) {
      return;
    }
    this.commentService.getComments(this.id);
  }
}
