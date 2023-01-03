import { Component, ElementRef, Input, OnChanges, OnInit, ViewChild } from '@angular/core';

import Comment from '../models/comment.model';
import { CommentService } from '../services/comments.service';
import EXAMPLE_COMMENTS from '../services/example-comments';

@Component({
  selector: 'app-comments',
  templateUrl: './comments.component.html',
  styleUrls: ['./comments.component.css'],
})
export class CommentsComponent implements OnInit, OnChanges {
  @Input() id!: string;
  @Input() startHeight!: number;
  @Input() startWidth!: number;
  @ViewChild('commentsElement') commentsElement!: ElementRef<HTMLDivElement>;
  comments: Comment[] = [];

  constructor(private commentService: CommentService) {}

  // TODO: subscribe/unsubscribe onDestroy?
  ngOnInit(): void {
    this.commentService.comments.subscribe((comments) => {
      // TODO: DELETE
      this.comments = EXAMPLE_COMMENTS;
    });
  }

  ngOnChanges(): void {
    if (this.id === undefined) {
      return;
    }
    this.commentService.getComments(this.id);
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
