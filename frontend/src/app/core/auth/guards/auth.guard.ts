import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { RouteFragment } from '../../routing/route-fragment';

export const authGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return authService.isLoggedIn()
    ? true
    : router.createUrlTree([
        RouteFragment.Slash,
        RouteFragment.Auth,
        RouteFragment.Login,
      ]);
};
