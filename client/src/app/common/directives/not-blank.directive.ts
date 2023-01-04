import { Directive } from '@angular/core';
import { FormGroup, NG_VALIDATORS, ValidationErrors, Validator } from '@angular/forms';

import notBlank from '../validators/not-blank.validator';

@Directive({
  selector: '[appNotBlank]',
  providers: [
    {
      provide: NG_VALIDATORS,
      useExisting: NotBlankDirective,
      multi: true,
    },
  ],
})
export class NotBlankDirective implements Validator {
  constructor() {}

  validate = (formGroup: FormGroup): ValidationErrors | null =>
    notBlank(formGroup);
}
