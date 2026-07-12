import { Routes } from '@angular/router';
import { authGuard } from '@core/auth/guards/auth.guard';
import { AppLayoutComponent } from './core/layouts/app-layout/app-layout.component';
import { RouteFragment } from './core/routing/route-fragment';

export const routes: Routes = [
  {
    path: RouteFragment.Auth,
    component: AppLayoutComponent,
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
      {
        path: RouteFragment.MyCoupons,
        canActivate: [authGuard],
        loadComponent: () =>
          import('./features/coupons/pages/coupons-page/coupons-page.component').then(
            (m) => m.CouponsPageComponent,
          ),
      },
    ],
  },
  {
    path: '**',
    redirectTo: RouteFragment.App,
  },
];
