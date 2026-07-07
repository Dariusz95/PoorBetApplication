import { TranslocoService } from '@jsverse/transloco';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { ErrorValueMap } from '../types/error-value-map';
import { ValidationErrorPipe } from './validation-error.pipe';

describe('ValidationErrorPipe', () => {
  let pipe: ValidationErrorPipe;
  let transloco: { translate: ReturnType<typeof vi.fn> };

  beforeEach(() => {
    transloco = { translate: vi.fn().mockReturnValue('translated') };
    pipe = new ValidationErrorPipe(transloco as unknown as TranslocoService);
  });

  it('should translate the required error with no params', () => {
    const result = pipe.transform({ required: true } as ErrorValueMap);

    expect(transloco.translate).toHaveBeenCalledWith(
      'validation.required',
      undefined,
    );
    expect(result).toBe('translated');
  });

  it('should translate the email error with no params', () => {
    pipe.transform({ email: true } as ErrorValueMap);

    expect(transloco.translate).toHaveBeenCalledWith(
      'validation.email',
      undefined,
    );
  });

  it('should pass minLength as a param for the minlength error', () => {
    pipe.transform({
      minlength: { requiredLength: 8, actualLength: 3 },
    } as ErrorValueMap);

    expect(transloco.translate).toHaveBeenCalledWith('validation.minlength', {
      minLength: 8,
    });
  });

  it('should pass maxLength as a param for the maxlength error', () => {
    pipe.transform({
      maxlength: { requiredLength: 20, actualLength: 30 },
    } as ErrorValueMap);

    expect(transloco.translate).toHaveBeenCalledWith('validation.maxlength', {
      maxLength: 20,
    });
  });

  it('should only translate the first error key when multiple are present', () => {
    pipe.transform({
      required: true,
      email: true,
    } as ErrorValueMap);

    expect(transloco.translate).toHaveBeenCalledWith(
      'validation.required',
      undefined,
    );
    expect(transloco.translate).toHaveBeenCalledTimes(1);
  });
});
