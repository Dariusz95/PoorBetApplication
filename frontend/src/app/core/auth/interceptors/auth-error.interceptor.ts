import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { RouteFragment } from '../../routing/route-fragment';
import { AuthService } from '../services/auth.service';

export const authErrorInterceptor: HttpInterceptorFn = (request, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return next(request).pipe(
    catchError((error: HttpErrorResponse) => {
      const isAuthRequest =
        request.url.includes('/login') || request.url.includes('/register');

      if (error.status === 401 && !isAuthRequest) {
        authService.logout();
        router.navigate([
          RouteFragment.Slash,
          RouteFragment.Auth,
          RouteFragment.Login,
        ]);
      }

      return throwError(() => error);
    })
  );
};
