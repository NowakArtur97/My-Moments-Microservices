import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { BackgroundTilesComponent } from './background/background-tiles/background-tiles.component';
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
  declarations: [
    validationDirectives,
    BackgroundComponent,
    BackgroundTilesComponent,
  ],
  imports: [
    CommonModule,
    BrowserModule,
    HttpClientModule,
    BrowserAnimationsModule,
  ],
  exports: [
    validationDirectives,
    CommonModule,
    HttpClientModule,
    BackgroundComponent,
  ],
})
export class AppCommonModule {}
