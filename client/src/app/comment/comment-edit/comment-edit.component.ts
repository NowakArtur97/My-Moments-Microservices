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
  private readonly CONTENT_ANIMATION_SPEED = 100;
  commentDTO: CommentDTO = { ...this.DEFAULT_COMMENT_VALUE };
  isEditingComment = false;
  letterIndex = 0;
  content = '';

  constructor(private commentService: CommentService) {}

  ngOnInit(): void {
    this.commentService.editComment.subscribe(({ content }) => {
      this.isEditingComment = content !== '';
      this.content = content;
      this.commentDTO.content = '';
      this.letterIndex = 0;
      this.animateTextContent();
    });
  }

  onAddComment(): void {
    this.commentService.addComment(this.postId, this.commentDTO);
    this.commentForm.resetForm();
    this.commentDTO = { ...this.DEFAULT_COMMENT_VALUE };
  }

  private animateTextContent(): void {
    this.commentDTO.content += this.content.charAt(this.letterIndex);
    this.letterIndex++;
    setTimeout(() => this.animateTextContent(), this.CONTENT_ANIMATION_SPEED);
  }
}
