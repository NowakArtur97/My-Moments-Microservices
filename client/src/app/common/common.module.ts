import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { NoWhiteSpacesDirective } from './directives/no-white-spaces.directive';

@NgModule({
  declarations: [NoWhiteSpacesDirective],
  imports: [CommonModule],
  exports: [NoWhiteSpacesDirective],
})
export class AppCommonModule {}
