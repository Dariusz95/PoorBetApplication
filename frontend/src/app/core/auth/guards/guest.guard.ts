import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { RouteLink } from '@core/routing/route-link';
import { RoutePath } from '@core/routing/route-path';
import { AuthService } from '../services/auth.service';

export const guestGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return authService.isLoggedIn()
    ? router.createUrlTree(RouteLink[RoutePath.App])
    : true;
};
