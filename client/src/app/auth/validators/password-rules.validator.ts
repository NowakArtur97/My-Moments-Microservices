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

const withUpperCaseRule = (
  formControl: AbstractControl
): ValidationErrors | null =>
  /[A-Z]+/.test(formControl.value) ? null : { withoutUppercase: true };

const withLowerCaseRule = (
  formControl: AbstractControl
): ValidationErrors | null =>
  /[a-z]+/.test(formControl.value) ? null : { withoutUppercase: true };

const withSpecialCharacterRule = (
  formControl: AbstractControl
): ValidationErrors | null =>
  /[\\!"#\$%&'()*\+,-.\/:;<=>?@\[\]^_`{|}~]/.test(formControl.value)
    ? null
    : { withoutUppercase: true };

function passwordRules(formControl: AbstractControl): ValidationErrors | null {
  return [
    noWhiteSpacesRule,
    uncommonRule,
    withUpperCaseRule,
    withLowerCaseRule,
    withSpecialCharacterRule,
  ]
    .map((validator) => {
      return validator(formControl);
    })
    .filter((result) => !!result);
}

export default passwordRules;
