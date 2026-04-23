import { Routes } from '@angular/router';
import { RouteFragment } from '../routing/route-fragment';

export const AUTH_ROUTES: Routes = [
  {
    path: RouteFragment.Login,
    loadComponent: () =>
      import('./login/login-page/login-page.component').then(
        (m) => m.LoginPageComponent,
      ),
  },
  {
    path: RouteFragment.Register,
    loadComponent: () =>
      import('./components/register/register.component').then(
        (m) => m.RegisterComponent,
      ),
  },
  {
    path: RouteFragment.WildCard,
    redirectTo: RouteFragment.Login,
  },
];
