import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';

import { AppCommonModule } from '../common/common.module';
import { AuthWrapperComponent } from './auth-wrapper/auth-wrapper.component';
import { AuthenticationComponent } from './authentication/authentication.component';
import { PasswordRulesDirective } from './directives/password-rules.directive';
import { RegistrationComponent } from './registration/registration.component';

@NgModule({
  declarations: [
    RegistrationComponent,
    PasswordRulesDirective,
    AuthenticationComponent,
    AuthWrapperComponent,
  ],
  imports: [FormsModule, BrowserModule, HttpClientModule, AppCommonModule],
  exports: [AuthWrapperComponent],
})
export class AuthModule {}
