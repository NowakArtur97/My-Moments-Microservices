import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { CommentsComponent } from './comments/comments.component';
import { CommentComponent } from './comment/comment.component';

@NgModule({
  declarations: [CommentsComponent, CommentComponent],
  imports: [CommonModule],
})
export class CommentsModule {}
