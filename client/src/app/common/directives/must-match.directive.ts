import { Directive, Input } from '@angular/core';
import { FormGroup, NG_VALIDATORS, ValidationErrors, Validator } from '@angular/forms';

import mustMatch from '../validators/must-match.validator';

@Directive({
  selector: '[appMustMatch]',
  providers: [
    { provide: NG_VALIDATORS, useExisting: MustMatchDirective, multi: true },
  ],
})
export class MustMatchDirective implements Validator {
  @Input('appMustMatch') mustMatch: string[] = [];

  constructor() {}

  validate = (formGroup: FormGroup): ValidationErrors | null =>
    mustMatch(this.mustMatch[0], this.mustMatch[1])(formGroup);
}
