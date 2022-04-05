import { NgModule } from '@angular/core';

import { AppCommonModule } from '../common/common.module';
import { PostWrapperComponent } from './post-wrapper/post-wrapper.component';

@NgModule({
  declarations: [PostWrapperComponent],
  imports: [AppCommonModule],
  exports: [PostWrapperComponent],
})
export class PostModule {}
