import {
  animate,
  state,
  style,
  transition,
  trigger,
} from '@angular/animations';
import { CommonModule } from '@angular/common';
import {
  ChangeDetectionStrategy,
  Component,
  computed,
  input,
  Signal,
} from '@angular/core';
import { TranslocoPipe } from '@jsverse/transloco';
import { MaxLengthError, MinLengthError } from './constants/error-types';
import { ErrorType } from './types/error-type';
import { ErrorValue, ErrorValueMap } from './types/error-value-map';
import { FormError } from './types/form-error';

@Component({
  selector: 'pb-errors',
  imports: [CommonModule, TranslocoPipe],
  templateUrl: './pb-errors.component.html',
  styleUrl: './pb-errors.component.scss',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  animations: [
    trigger('errorState', [
      state(
        'void',
        style({
          opacity: 0,
          transform: 'translateY(-10px)',
        })
      ),
      state(
        '*',
        style({
          opacity: 1,
          transform: 'translateY(0)',
        })
      ),
      transition('void <=> *', animate('200ms ease-in-out')),
    ]),
  ],
})
export class PbErrorsComponent {
  errors = input.required<ErrorValueMap>();
  showErrors = input(true);
  displaySingleError = input(true);

  readonly formErrors: Signal<FormError[]> = computed(() => {
    const errorEntries = Object.entries(this.errors());

    if (errorEntries.length === 0) return [];

    const slicedErrors = this.displaySingleError()
      ? [errorEntries[0]]
      : errorEntries;

    return slicedErrors.map(([key, value]) =>
      this.mapToFormError({
        [key as ErrorType]: value,
      } as ErrorValueMap)
    );
  });

  private mapToFormError(error: ErrorValueMap): FormError {
    const errorType = Object.keys(error)[0] as ErrorType;
    const value = error[errorType] as ErrorValue<typeof errorType>;

    let formError: FormError = {
      translateKey: `validation.${errorType}`,
      params: {},
    };

    switch (errorType) {
      case 'minlength':
        formError.params = {
          minLength: (value as MinLengthError).requiredLength,
        };
        break;

      case 'maxlength':
        formError.params = {
          maxLength: (value as MaxLengthError).requiredLength,
        };
        break;
    }
    console.log(formError);

    return formError;
  }
}
