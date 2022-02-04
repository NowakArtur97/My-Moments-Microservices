import { Directive, Input } from '@angular/core';
import {
  FormGroup,
  NG_VALIDATORS,
  ValidationErrors,
  Validator,
} from '@angular/forms';

import notInclude from '../validators/not-include.validator';

@Directive({
  selector: '[appNotInclude]',
  providers: [
    { provide: NG_VALIDATORS, useExisting: NotIncludeDirective, multi: true },
  ],
})
export class NotIncludeDirective implements Validator {
  @Input('appNotInclude') notInclude: string[] = [];

  constructor() {}

  validate = (formGroup: FormGroup): ValidationErrors | null =>
    notInclude(this.notInclude[0], this.notInclude[1])(formGroup);
}
