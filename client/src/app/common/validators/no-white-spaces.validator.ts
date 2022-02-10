import { AbstractControl, ValidationErrors } from '@angular/forms';

const WHITE_SPACE = ' ';

const noWhiteSpaces = (formControl: AbstractControl): ValidationErrors | null =>
  String(formControl.value).includes(WHITE_SPACE)
    ? { hasWhiteSpaces: true }
    : null;

export default noWhiteSpaces;
