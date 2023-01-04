import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { AppCommonModule } from '../common/common.module';
import { CommentEditComponent } from './comment-edit/comment-edit.component';
import { CommentComponent } from './comment/comment.component';
import { CommentsComponent } from './comments/comments.component';

@NgModule({
  declarations: [CommentsComponent, CommentComponent, CommentEditComponent],
  imports: [FormsModule, AppCommonModule],
  exports: [CommentsComponent],
})
export class CommentsModule {}
