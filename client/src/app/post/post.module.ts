import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { CommentsModule } from '../comment/comments.module';
import { AppCommonModule } from '../common/common.module';
import { FollowingPostsComponent } from './following-posts/following-posts.component';
import { MyPostsComponent } from './my-posts/my-posts.component';
import { PostEditComponent } from './post-edit/post-edit.component';
import { PostWrapperComponent } from './post-wrapper/post-wrapper.component';
import { PostComponent } from './post/post.component';

@NgModule({
  declarations: [
    PostWrapperComponent,
    PostEditComponent,
    PostComponent,
    MyPostsComponent,
    FollowingPostsComponent,
  ],
  imports: [FormsModule, AppCommonModule, CommentsModule],
  exports: [PostWrapperComponent],
})
export class PostModule {}
