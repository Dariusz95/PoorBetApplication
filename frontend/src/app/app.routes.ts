import { Routes } from '@angular/router';
import { guestGuard } from '@core/auth/guards/guest.guard';
import { AppLayoutComponent } from './core/layouts/app-layout/app-layout.component';
import { RouteFragment } from './core/routing/route-fragment';

export const routes: Routes = [
  {
    path: RouteFragment.Auth,
    component: AppLayoutComponent,
    canActivate: [guestGuard],
    children: [
      {
        path: '',
        loadChildren: () =>
          import('./core/auth/auth.routes').then((m) => m.AUTH_ROUTES),
      },
      {
        path: '**',
        redirectTo: RouteFragment.Auth,
      },
    ],
  },
  {
    path: RouteFragment.App,
    component: AppLayoutComponent,
    children: [
      {
        path: '',
        loadComponent: () =>
          import('./features/bet/bet-page.component').then(
            (m) => m.BetPageComponent,
          ),
      },
    ],
  },
  {
    path: '**',
    redirectTo: RouteFragment.App,
  },
];
