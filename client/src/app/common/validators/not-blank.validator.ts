import { AbstractControl, ValidationErrors } from '@angular/forms';

const notBlank = (formControl: AbstractControl): ValidationErrors | null =>
  String(formControl.value).replace(/\s/g, '').length === 0
    ? { isBlank: true }
    : null;

export default notBlank;
