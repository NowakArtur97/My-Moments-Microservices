import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { AppCommonModule } from '../common/common.module';
import { PostEditComponent } from './post-edit/post-edit.component';
import { PostWrapperComponent } from './post-wrapper/post-wrapper.component';

@NgModule({
  declarations: [PostWrapperComponent, PostEditComponent],
  imports: [FormsModule, HttpClientModule, AppCommonModule],
  exports: [PostWrapperComponent],
})
export class PostModule {}
