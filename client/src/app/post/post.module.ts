import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { CommentsModule } from '../comments/comments.module';
import { AppCommonModule } from '../common/common.module';
import { PostEditComponent } from './post-edit/post-edit.component';
import { PostWrapperComponent } from './post-wrapper/post-wrapper.component';
import { PostComponent } from './post/post.component';
import { PostsComponent } from './posts/posts.component';

@NgModule({
  declarations: [
    PostWrapperComponent,
    PostEditComponent,
    PostsComponent,
    PostComponent,
  ],
  imports: [FormsModule, AppCommonModule, CommentsModule],
  exports: [PostWrapperComponent],
})
export class PostModule {}
