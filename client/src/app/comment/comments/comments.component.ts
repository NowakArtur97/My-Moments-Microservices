import { Component, ElementRef, Input, OnInit, ViewChild } from '@angular/core';

import Comment from '../models/comment.model';
import { CommentService } from '../services/comments.service';

@Component({
  selector: 'app-comments',
  templateUrl: './comments.component.html',
  styleUrls: ['./comments.component.css'],
})
export class CommentsComponent implements OnInit {
  @Input() postId!: string;
  @Input() startHeight!: number;
  @Input() startWidth!: number;
  @ViewChild('commentsElement') commentsElement!: ElementRef<HTMLDivElement>;
  comments: Comment[] = [];

  constructor(private commentService: CommentService) {}

  // TODO: subscribe/unsubscribe onDestroy?
  ngOnInit(): void {
    this.commentService.comments.subscribe((comments) => {
      this.comments = comments;
      // TODO: DELETE
      // this.comments = EXAMPLE_COMMENTS;
    });
  }

  getSize(): { height: string; width: string } {
    return {
      height: `${this.startHeight}px`,
      width: `${this.startWidth}px`,
    };
  }

  getCommentWidth(): { width: string } {
    return {
      width: `${this.startWidth}px`,
    };
  }
}
