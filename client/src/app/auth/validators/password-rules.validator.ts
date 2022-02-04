import { AbstractControl, ValidationErrors } from '@angular/forms';

import commonPasswords from './common-passwords-list';

const WHITE_SPACE = ' ';

const noWhiteSpacesRule = (
  formControl: AbstractControl
): ValidationErrors | null =>
  String(formControl.value).includes(WHITE_SPACE)
    ? { hasWhiteSpaces: true }
    : null;

const uncommonRule = (formControl: AbstractControl): ValidationErrors | null =>
  commonPasswords.includes(String(formControl.value))
    ? { commonPassword: true }
    : null;

function passwordRules(formControl: AbstractControl): ValidationErrors | null {
  return [noWhiteSpacesRule, uncommonRule]
    .map((validator) => {
      return validator(formControl);
    })
    .filter((result) => !!result);
}

export default passwordRules;
