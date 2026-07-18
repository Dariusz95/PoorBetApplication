import { HttpErrorResponse } from '@angular/common/http';
import { ApiError } from '@shared/types/api-error';

export const extractApiError = (error: HttpErrorResponse): ApiError | null => {
  const body: unknown = error.error;

  if (
    body !== null &&
    typeof body === 'object' &&
    typeof (body as ApiError).code === 'string' &&
    typeof (body as ApiError).message === 'string'
  ) {
    return body as ApiError;
  }

  return null;
};
