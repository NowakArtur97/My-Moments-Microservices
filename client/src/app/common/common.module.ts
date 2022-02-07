import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { MustMatchDirective } from './directives/must-match.directive';
import { NotIncludeDirective } from './directives/not-include.directive';

const validationDirectives = [NotIncludeDirective, MustMatchDirective];

@NgModule({
  declarations: [validationDirectives],
  imports: [CommonModule],
  exports: [validationDirectives],
})
export class AppCommonModule {}
