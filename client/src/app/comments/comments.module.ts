import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { CommentComponent } from './comment/comment.component';
import { CommentsComponent } from './comments/comments.component';

@NgModule({
  declarations: [CommentsComponent, CommentComponent],
  imports: [CommonModule],
  exports: [CommentsComponent],
})
export class CommentsModule {}
