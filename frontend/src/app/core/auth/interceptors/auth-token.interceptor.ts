import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { JwtAuthStateService } from '../services/jwt-auth-state.service';

export const authTokenInterceptor: HttpInterceptorFn = (request, next) => {
  const jwtAuthStateService = inject(JwtAuthStateService);
  const token = jwtAuthStateService.getToken();

  if (!token) {
    return next(request);
  }

  return next(
    request.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`,
      },
    })
  );
};
