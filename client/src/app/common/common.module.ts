import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { BackgroundComponent } from './background/background.component';
import { MustMatchDirective } from './directives/must-match.directive';
import { NoWhiteSpacesDirective } from './directives/no-white-spaces.directive';
import { NotIncludeDirective } from './directives/not-include.directive';

const validationDirectives = [
  NotIncludeDirective,
  MustMatchDirective,
  NoWhiteSpacesDirective,
];

@NgModule({
  declarations: [validationDirectives, BackgroundComponent],
  imports: [CommonModule, BrowserAnimationsModule],
  exports: [validationDirectives, BackgroundComponent],
})
export class AppCommonModule {}
