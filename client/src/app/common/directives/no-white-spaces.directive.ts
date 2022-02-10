import { Directive } from '@angular/core';
import { FormGroup, NG_VALIDATORS, ValidationErrors, Validator } from '@angular/forms';

import noWhiteSpaces from '../validators/no-white-spaces.validator';

@Directive({
  selector: '[appNoWhiteSpaces]',
  providers: [
    {
      provide: NG_VALIDATORS,
      useExisting: NoWhiteSpacesDirective,
      multi: true,
    },
  ],
})
export class NoWhiteSpacesDirective implements Validator {
  constructor() {}

  validate = (formGroup: FormGroup): ValidationErrors | null =>
    noWhiteSpaces(formGroup);
}
