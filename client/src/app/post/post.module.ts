import { NgModule } from '@angular/core';

import { AppCommonModule } from '../common/common.module';
import { PostWrapperComponent } from './post-wrapper/post-wrapper.component';
import { PostEditComponent } from './post-edit/post-edit.component';

@NgModule({
  declarations: [PostWrapperComponent, PostEditComponent],
  imports: [AppCommonModule],
  exports: [PostWrapperComponent],
})
export class PostModule {}
