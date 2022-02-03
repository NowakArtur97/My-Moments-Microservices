import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { RegistrationComponent } from './registration/registration.component';

@NgModule({
  declarations: [RegistrationComponent],
  imports: [CommonModule, FormsModule],
  exports: [RegistrationComponent],
})
export class AuthModule {}
