import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { NgForm } from '@angular/forms';

import CommentDTO from '../models/comment.dto';
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
  commentDTO: CommentDTO = { ...this.DEFAULT_COMMENT_VALUE };
  isEditingComment = false;
  letterIndex = 0;
  content = '';
  private textContentAppearTimeout: Array<NodeJS.Timeout> = [];
  private textContentDisappearTimeout: Array<NodeJS.Timeout> = [];

  constructor(private commentService: CommentService) {}

  ngOnInit(): void {
    this.commentService.editComment.subscribe(({ content }) => {
      this.content = content;
      if (content !== '') {
        this.startEditingComment();
      } else {
        this.stopEditingComment();
      }
    });
  }

  onAddComment(): void {
    this.commentService.addComment(this.postId, this.commentDTO);
    this.commentForm.resetForm();
    this.commentDTO = { ...this.DEFAULT_COMMENT_VALUE };
  }

  private startEditingComment() {
    this.isEditingComment = true;
    this.textContentDisappearTimeout.forEach((timeout) =>
      clearTimeout(timeout)
    );
    this.textContentAppearTimeout = [];
    this.commentDTO.content = '';
    this.letterIndex = 0;
    this.animateTextContentAppearance();
  }

  private stopEditingComment() {
    this.isEditingComment = false;
    this.textContentAppearTimeout.forEach((timeout) => clearTimeout(timeout));
    this.textContentAppearTimeout = [];
    this.animateTextContentDisappearance();
  }

  private animateTextContentAppearance(): void {
    this.commentDTO.content += this.content.charAt(this.letterIndex);
    this.letterIndex++;
    this.textContentAppearTimeout.push(
      setTimeout(
        () => this.animateTextContentAppearance(),
        this.CONTENT_TEXT_ANIMATION_SPEED
      )
    );
  }

  private animateTextContentDisappearance(): void {
    this.commentDTO.content = this.commentDTO.content.slice(0, -1);
    this.textContentDisappearTimeout.push(
      setTimeout(
        () => this.animateTextContentDisappearance(),
        this.CONTENT_TEXT_ANIMATION_SPEED
      )
    );
  }
}
