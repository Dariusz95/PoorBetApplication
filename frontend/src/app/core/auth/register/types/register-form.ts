import { FormControl } from '@angular/forms';

export type RegisterForm = {
  email: FormControl<string>;
  password: FormControl<string>;
  confirmPassword: FormControl<string>;
};
