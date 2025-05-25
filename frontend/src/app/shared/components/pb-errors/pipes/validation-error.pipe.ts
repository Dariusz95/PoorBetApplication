import { Pipe, PipeTransform } from '@angular/core';
import { TranslocoService } from '@jsverse/transloco';
import { MinLengthError } from '../constants/error-types';
import { ErrorType } from '../types/error-type';
import { ErrorValue, ErrorValueMap } from '../types/error-value-map';

@Pipe({
  name: 'validationError',
  standalone: true,
})
export class ValidationErrorPipe implements PipeTransform {
  constructor(private transloco: TranslocoService) {}

  transform(error: ErrorValueMap): string {
    const type = Object.keys(error)[0] as ErrorType;
    const value = error[type] as ErrorValue<typeof type>;

    const translateKey = `validation.${type}`;
    let params: Record<string, any> | undefined;

    switch (type) {
      case 'minlength':
        params = {
          minLength: (value as MinLengthError).requiredLength,
        };
        break;

      case 'maxlength':
        params = {
          maxLength: (value as MinLengthError).requiredLength,
        };
        break;

      case 'required':
      case 'email':
      case 'pattern':
      default:
        break;
    }

    return this.transloco.translate(translateKey, params);
  }
}
