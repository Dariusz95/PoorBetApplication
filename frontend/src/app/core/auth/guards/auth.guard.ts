import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { RouteFragment } from '../../routing/route-fragment';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return authService.isLoggedIn()
    ? true
    : router.createUrlTree([RouteFragment.Slash, RouteFragment.Auth]);
};
