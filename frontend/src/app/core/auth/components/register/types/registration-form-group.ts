import { FormControl, FormGroup } from "@angular/forms";

export type RegistrationFormGroup = FormGroup<{
  email: FormControl<string>;
  password: FormControl<string>;
  confirmPassword: FormControl<string>;
}>;
