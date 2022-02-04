import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { AppCommonModule } from '../common/common.module';
import { PasswordRulesDirective } from './directives/password-rules.directive';
import { RegistrationComponent } from './registration/registration.component';

@NgModule({
  declarations: [RegistrationComponent, PasswordRulesDirective],
  imports: [FormsModule, AppCommonModule],
  exports: [RegistrationComponent],
})
export class AuthModule {}
