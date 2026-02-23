import { Routes } from '@angular/router';
import { RouteFragment } from '../routing/route-fragment';
import { guestGuard } from './guards/guest.guard';

export const AUTH_ROUTES: Routes = [
  {
    path: RouteFragment.Login,
    canActivate: [guestGuard],
    loadComponent: () =>
      import('./components/login/login.component').then(
        (m) => m.LoginComponent
      ),
  },
  {
    path: RouteFragment.Register,
    canActivate: [guestGuard],
    loadComponent: () =>
      import('./components/register/register.component').then(
        (m) => m.RegisterComponent
      ),
  },
  {
    path: RouteFragment.WildCard,
    redirectTo: `${RouteFragment.Auth}/${RouteFragment.Register}`,
  },
];
