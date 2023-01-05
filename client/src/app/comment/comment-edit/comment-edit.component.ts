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

  commentDTO: CommentDTO = {
    content: '',
  };

  constructor(private commentService: CommentService) {}

  ngOnInit(): void {}

  onAddComment(): void {
    this.commentService.addComment(this.postId, this.commentDTO);
    this.commentForm.resetForm();
  }
}
