import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { NotIncludeDirective } from './directives/not-include.directive';

@NgModule({
  declarations: [NotIncludeDirective],
  imports: [CommonModule],
  exports: [NotIncludeDirective],
})
export class AppCommonModule {}
