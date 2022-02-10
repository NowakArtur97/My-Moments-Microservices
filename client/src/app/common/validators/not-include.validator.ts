import { FormGroup, ValidationErrors } from '@angular/forms';

function notInclude(
  controlNameToCheck: string,
  controlNameToNotBeIncluded: string
) {
  return (formGroup: FormGroup): ValidationErrors | null => {
    const controlToCheck = formGroup.controls[controlNameToCheck];
    const controlToNotBeIncluded =
      formGroup.controls[controlNameToNotBeIncluded];

    if (!controlToCheck || !controlToNotBeIncluded) {
      return null;
    }

    const controlToCheckValue = controlToCheck.value;
    const controlToNotBeIncludedValue = controlToNotBeIncluded.value;
    if (
      controlToCheckValue &&
      controlToNotBeIncludedValue &&
      String(controlToCheckValue)
        .toLowerCase()
        .includes(String(controlToNotBeIncludedValue).toLowerCase())
    ) {
      controlToCheck.setErrors({
        ...controlToCheck.errors,
        shouldNotInclude: true,
      });
    }
    return null;
  };
}

export default notInclude;
