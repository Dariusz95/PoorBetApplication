import { Routes } from '@angular/router';
import { RouteFragment } from './core/routing/route-fragment';

export const routes: Routes = [
  {
    path: RouteFragment.Auth,
    loadChildren: () =>
      import('./core/auth/auth.routes').then((m) => m.AUTH_ROUTES),
  },
  { path: '**', redirectTo: 'auth/register' },
];
