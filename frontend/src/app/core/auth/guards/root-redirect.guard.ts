import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { RouteFragment } from '../../routing/route-fragment';

export const rootRedirectGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return authService.isLoggedIn()
    ? router.createUrlTree([RouteFragment.Slash, RouteFragment.App])
    : router.createUrlTree([
        RouteFragment.Slash,
        RouteFragment.Auth,
        RouteFragment.Login,
      ]);
};
