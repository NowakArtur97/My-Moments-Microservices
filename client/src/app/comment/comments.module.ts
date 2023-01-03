import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { CommentEditComponent } from './comment-edit/comment-edit.component';
import { CommentComponent } from './comment/comment.component';
import { CommentsComponent } from './comments/comments.component';

@NgModule({
  declarations: [CommentsComponent, CommentComponent, CommentEditComponent],
  imports: [CommonModule],
  exports: [CommentsComponent],
})
export class CommentsModule {}
