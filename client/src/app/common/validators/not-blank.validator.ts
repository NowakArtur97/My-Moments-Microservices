import { AbstractControl, ValidationErrors } from '@angular/forms';

const WHITE_SPACE = ' ';

const notBlank = (formControl: AbstractControl): ValidationErrors | null =>
  String(formControl.value).includes(WHITE_SPACE)
    ? { hasWhiteSpaces: true }
    : null;

export default notBlank;
