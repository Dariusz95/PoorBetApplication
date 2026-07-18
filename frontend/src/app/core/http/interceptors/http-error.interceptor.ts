import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { TranslocoService } from '@jsverse/transloco';
import { ToastService } from '@shared/services/toast.service';
import { extractApiError } from '@shared/utils/extract-api-error.util';
import { catchError, throwError } from 'rxjs';
import { apiErrorTranslationKey } from '../utils/api-error-translation.util';
import { SKIP_ERROR_TOAST } from '../tokens/skip-error-toast.token';

export const httpErrorInterceptor: HttpInterceptorFn = (request, next) => {
  const toastService = inject(ToastService);
  const transloco = inject(TranslocoService);

  return next(request).pipe(
    catchError((error: HttpErrorResponse) => {
      if (request.context.get(SKIP_ERROR_TOAST)) {
        return throwError(() => error);
      }

      const apiError = extractApiError(error);

      toastService.error(
        transloco.translate(apiErrorTranslationKey(apiError?.code)),
      );

      return throwError(() => error);
    }),
  );
};
