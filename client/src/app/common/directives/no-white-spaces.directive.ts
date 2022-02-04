import { Directive } from '@angular/core';
import {
  AbstractControl,
  NG_VALIDATORS,
  ValidationErrors,
  Validator,
} from '@angular/forms';

import noWhiteSpaces from '../validators/white-space.validator';

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

  validate = (control: AbstractControl): ValidationErrors | null =>
    noWhiteSpaces(control);
}
