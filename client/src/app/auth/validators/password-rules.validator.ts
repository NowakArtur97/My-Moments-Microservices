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
  /[A-Z]+/.test(formControl.value) ? null : { withoutUpperCase: true };

const withLowerCaseRule = (
  formControl: AbstractControl
): ValidationErrors | null =>
  /[a-z]+/.test(formControl.value) ? null : { withoutUpperLase: true };

const withSpecialCharacterRule = (
  formControl: AbstractControl
): ValidationErrors | null =>
  /[\\!"#\$%&'()*\+,-.\/:;<=>?@\[\]^_`{|}~]/.test(formControl.value)
    ? null
    : { withoutSpecialCharacter: true };

const without3RepeatedCharactersRule = (
  formControl: AbstractControl
): ValidationErrors | null =>
  /(.)\1{2,}/.test(formControl.value)
    ? { with3RepeatedCharacters: true }
    : null;

function passwordRules(formControl: AbstractControl): ValidationErrors | null {
  return [
    noWhiteSpacesRule,
    uncommonRule,
    withUpperCaseRule,
    withLowerCaseRule,
    withSpecialCharacterRule,
    without3RepeatedCharactersRule,
  ]
    .map((validator) => 
       validator(formControl)
    )
    .filter((result) => !!result);
}

export default passwordRules;
