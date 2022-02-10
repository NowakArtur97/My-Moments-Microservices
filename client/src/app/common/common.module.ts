import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { MustMatchDirective } from './directives/must-match.directive';
import { NotBlankDirective } from './directives/not-blank.directive';
import { NotIncludeDirective } from './directives/not-include.directive';

const validationDirectives = [
  NotIncludeDirective,
  MustMatchDirective,
  NotBlankDirective,
];

@NgModule({
  declarations: [validationDirectives],
  imports: [CommonModule],
  exports: [validationDirectives],
})
export class AppCommonModule {}
