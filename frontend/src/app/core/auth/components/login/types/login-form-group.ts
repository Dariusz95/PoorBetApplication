import { FormControl, FormGroup } from "@angular/forms";

export type LoginFormGroup = FormGroup<{
  email: FormControl<string>;
  password: FormControl<string>;
}>;
