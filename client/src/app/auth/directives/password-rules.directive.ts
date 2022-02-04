import { Directive } from '@angular/core';
import {
  AbstractControl,
  NG_VALIDATORS,
  ValidationErrors,
  Validator,
} from '@angular/forms';

import passwordRules from '../validators/password-rules.validator';

@Directive({
  selector: '[appPasswordRules]',
  providers: [
    {
      provide: NG_VALIDATORS,
      useExisting: PasswordRulesDirective,
      multi: true,
    },
  ],
})
export class PasswordRulesDirective implements Validator {
  constructor() {}

  validate = (control: AbstractControl): ValidationErrors | null =>
    passwordRules(control);
}
