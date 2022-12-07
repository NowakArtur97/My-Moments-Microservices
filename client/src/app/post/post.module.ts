import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { AppCommonModule } from '../common/common.module';
import { PostEditComponent } from './post-edit/post-edit.component';
import { PostWrapperComponent } from './post-wrapper/post-wrapper.component';
import { PostsComponent } from './posts/posts.component';
import { PostComponent } from './post/post.component';

@NgModule({
  declarations: [PostWrapperComponent, PostEditComponent, PostsComponent, PostComponent],
  imports: [FormsModule, AppCommonModule],
  exports: [PostWrapperComponent],
})
export class PostModule {}
