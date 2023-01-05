import { Component, Input, OnInit } from '@angular/core';

import Comment from '../models/comment.model';

@Component({
  selector: 'app-comment',
  templateUrl: './comment.component.html',
  styleUrls: ['./comment.component.css'],
})
export class CommentComponent implements OnInit {
  @Input() comment!: Comment;

  constructor() {}

  ngOnInit(): void {}

  getFormatedDate = (): string =>
    // DD/MM/YYYY
    new Date(this.comment.createDate).toLocaleDateString('pt-PT');
}
