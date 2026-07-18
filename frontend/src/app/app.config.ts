import {
  ApplicationConfig,
  isDevMode,
  provideZoneChangeDetection,
} from '@angular/core';
import { provideRouter, TitleStrategy } from '@angular/router';

import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideAnimations } from '@angular/platform-browser/animations';
import { provideTransloco } from '@jsverse/transloco';
import { provideToastr } from 'ngx-toastr';
import { routes } from './app.routes';
import { authErrorInterceptor } from './core/auth/interceptors/auth-error.interceptor';
import { authTokenInterceptor } from './core/auth/interceptors/auth-token.interceptor';
import { httpErrorInterceptor } from './core/http/interceptors/http-error.interceptor';
import { TranslocoTitleStrategy } from './core/routing/transloco-title-strategy';
import { TranslocoHttpLoader } from './transloco-loader';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(
      withInterceptors([
        authTokenInterceptor,
        authErrorInterceptor,
        httpErrorInterceptor,
      ]),
    ),
    provideAnimations(),
    provideToastr({
      timeOut: 4000,
      positionClass: 'toast-top-right',
      preventDuplicates: true,
    }),
    provideTransloco({
      config: {
        availableLangs: ['en', 'pl'],
        defaultLang: 'en',
        reRenderOnLangChange: true,
        prodMode: !isDevMode(),
      },
      loader: TranslocoHttpLoader,
    }),
    { provide: TitleStrategy, useClass: TranslocoTitleStrategy },
  ],
};
