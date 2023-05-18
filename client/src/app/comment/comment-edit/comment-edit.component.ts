import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { NgForm } from '@angular/forms';

import CommentDTO from '../models/comment.dto';
import Comment from '../models/comment.model';
import { CommentService } from '../services/comments.service';

@Component({
  selector: 'app-comment-edit',
  templateUrl: './comment-edit.component.html',
  styleUrls: [
    './comment-edit.component.css',
    '../comment/comment.component.css',
  ],
})
export class CommentEditComponent implements OnInit {
  @Input() postId!: string;
  @ViewChild('commentForm') commentForm!: NgForm;
  private readonly DEFAULT_COMMENT_VALUE = {
    content: '',
  };
  private readonly CONTENT_TEXT_ANIMATION_SPEED = 80;
  private editedComment!: Comment;
  commentDTO: CommentDTO = { ...this.DEFAULT_COMMENT_VALUE };
  isEditingComment = false;
  letterIndex = 0;
  content = '';
  private textContentAppearTimeouts: Array<number> = [];
  private textContentDisappearTimeouts: Array<number> = [];

  constructor(private commentService: CommentService) {}

  ngOnInit(): void {
    this.commentService.editComment.subscribe((comment) => {
      const { content } = comment;
      this.content = content;
      if (content !== '') {
        if (comment.id === this.editedComment?.id) {
          return;
        }
        this.editedComment = comment;
        this.startEditingComment();
      } else {
        this.editedComment = comment;
        this.stopEditingComment();
      }
    });
  }

  onAddComment(): void {
    this.commentService.addComment(this.postId, this.commentDTO);
    this.commentForm.resetForm();
    this.commentDTO = { ...this.DEFAULT_COMMENT_VALUE };
  }

  private startEditingComment(): void {
    this.isEditingComment = true;
    this.clearTimeouts(this.textContentDisappearTimeouts);
    this.commentDTO.content = '';
    this.letterIndex = 0;
    this.animateTextContentAppearance();
  }

  private stopEditingComment(): void {
    this.isEditingComment = false;
    this.clearTimeouts(this.textContentAppearTimeouts);
    this.animateTextContentDisappearance();
  }

  private clearTimeouts(timeouts: Array<number>): void {
    timeouts.forEach((timeout) => clearTimeout(timeout));
    timeouts = [];
  }

  private animateTextContentAppearance(): void {
    this.commentDTO.content += this.content.charAt(this.letterIndex);
    this.letterIndex++;
    if (this.commentDTO.content.length === 0) {
      this.clearTimeouts(this.textContentAppearTimeouts);
    } else {
      this.textContentAppearTimeouts.push(
        window.setTimeout(
          () => this.animateTextContentAppearance(),
          this.CONTENT_TEXT_ANIMATION_SPEED
        )
      );
    }
  }

  private animateTextContentDisappearance(): void {
    this.commentDTO.content = this.commentDTO.content.slice(0, -1);
    if (this.commentDTO.content.length === 0) {
      this.clearTimeouts(this.textContentDisappearTimeouts);
    } else {
      this.textContentDisappearTimeouts.push(
        window.setTimeout(
          () => this.animateTextContentDisappearance(),
          this.CONTENT_TEXT_ANIMATION_SPEED
        )
      );
    }
  }
}
