import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { RegistrationComponent } from './registration/registration.component';

@NgModule({
  declarations: [RegistrationComponent],
  imports: [CommonModule],
  exports: [RegistrationComponent],
})
export class AuthModule {}
