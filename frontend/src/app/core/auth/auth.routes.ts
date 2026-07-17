import { Routes } from '@angular/router';
import { RouteFragment } from '../routing/route-fragment';

export const AUTH_ROUTES: Routes = [
  {
    path: RouteFragment.Login,
    title: 'pageTitle.login',
    loadComponent: () =>
      import('./login/login-page/login-page.component').then(
        (m) => m.LoginPageComponent,
      ),
  },
  {
    path: RouteFragment.Register,
    title: 'pageTitle.register',
    loadComponent: () =>
      import('./register/register-page/register-page.component').then(
        (m) => m.RegisterPageComponent,
      ),
  },
  {
    path: RouteFragment.WildCard,
    redirectTo: RouteFragment.Login,
  },
];
