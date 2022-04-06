import { NgModule } from '@angular/core';

import { AppRoutingModule } from '../app-routing.module';
import { AppCommonModule } from '../common/common.module';
import { PostEditComponent } from './post-edit/post-edit.component';
import { PostWrapperComponent } from './post-wrapper/post-wrapper.component';

@NgModule({
  declarations: [PostWrapperComponent, PostEditComponent],
  imports: [AppCommonModule, AppRoutingModule],
  exports: [PostWrapperComponent, PostEditComponent],
})
export class PostModule {}
