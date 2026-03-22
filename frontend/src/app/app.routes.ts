import { Routes } from '@angular/router';
import { AppLayoutComponent } from './core/layouts/app-layout/app-layout.component';
import { RouteFragment } from './core/routing/route-fragment';

export const routes: Routes = [
  // {
  //   path: '',
  //   // canActivate: [rootRedirectGuard],
  //   children: [],
  // },
  {
    path: '',
    // path: RouteFragment.App,
    component: AppLayoutComponent,
    //     canActivate: [authGuard],
    children: [
      {
        path: '',
        loadComponent: () =>
          import('./features/bet/bet-page/bet-page.component').then(
            (m) => m.BetPageComponent,
          ),
      },
    ],
  },
  {
    path: RouteFragment.Auth,
    component: AppLayoutComponent,
    children: [
      {
        path: '',
        loadChildren: () =>
          import('./core/auth/auth.routes').then((m) => m.AUTH_ROUTES),
      },
    ],
  },
  //   {
  //     path: RouteFragment.Auth,
  //     loadChildren: () =>
  //       import('./core/auth/auth.routes').then((m) => m.AUTH_ROUTES),
  //   },
  {
    path: '**',
    redirectTo: '',
  },
];
