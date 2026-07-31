import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, switchMap, throwError } from 'rxjs';
import { RouteFragment } from '../../routing/route-fragment';
import { AuthService } from '../services/auth.service';

export const authErrorInterceptor: HttpInterceptorFn = (request, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const isAuthRequest =
    request.url.includes('/login') ||
    request.url.includes('/register') ||
    request.url.includes('/refresh') ||
    request.url.includes('/logout');

  const redirectToLogin = (): void => {
    router.navigate([
      RouteFragment.Slash,
      RouteFragment.Auth,
      RouteFragment.Login,
    ]);
  };

  return next(request).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status !== 401 || isAuthRequest) {
        return throwError(() => error);
      }


      return authService.refresh().pipe(
        switchMap((response) =>
          next(
            request.clone({
              setHeaders: { Authorization: `Bearer ${response.token}` },
            })
          )
        ),
        catchError((refreshError) => {
          authService.logout();
          redirectToLogin();
          
          return throwError(() => refreshError);
        })
      );
    })
  );
};
