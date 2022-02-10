import { AbstractControl, ValidationErrors } from '@angular/forms';
import flattenObject from 'src/app/common/util/flaten-object.util';
import notBlank from 'src/app/common/validators/no-white-spaces.validator';

import commonPasswords from './common-passwords-list';

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
  /[a-z]+/.test(formControl.value) ? null : { withoutLowerCase: true };

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
  const errors = [
    notBlank,
    uncommonRule,
    withUpperCaseRule,
    withLowerCaseRule,
    withSpecialCharacterRule,
    without3RepeatedCharactersRule,
  ]
    .map((validator) => validator(formControl))
    .filter((result) => !!result);
  return flattenObject(errors);
}

export default passwordRules;
