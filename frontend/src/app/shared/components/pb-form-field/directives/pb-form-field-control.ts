import { InjectionToken, WritableSignal } from '@angular/core';
import { Observable } from 'rxjs';
import { ErrorValueMap } from '../../pb-errors/types/error-value-map';

export interface FormFieldControl {
  stateChanges: Observable<void>;
  focused: WritableSignal<boolean>;
  touched: WritableSignal<boolean>;
  disabled: WritableSignal<boolean>;
  errors: WritableSignal<ErrorValueMap | null>;
}

export const FORM_FIELD_CONTROL = new InjectionToken<FormFieldControl>(
  'FormFieldControl'
);
