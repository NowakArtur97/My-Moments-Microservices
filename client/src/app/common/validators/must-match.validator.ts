import { FormGroup, ValidationErrors } from '@angular/forms';

function mustMatch(controlName: string, matchingControlName: string): any {
  return (formGroup: FormGroup): ValidationErrors | null => {
    const control = formGroup.controls[controlName];
    const matchingControl = formGroup.controls[matchingControlName];
    if (control && matchingControl && control.value !== matchingControl.value) {
      matchingControl.setErrors({
        ...matchingControl.errors,
        mustMatch: true,
      });
    }

    return null;
  };
}

export default mustMatch;
