import { inject, Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { RouteData } from './route-data';
import { RouteLink } from './route-link';
import { RouteParam } from './route-param';
import { RoutePath } from './route-path';

@Injectable({ providedIn: 'root' })
export class RoutingService {
  private readonly router = inject(Router);

  createLink(
    path: RoutePath,
    params?: Partial<Record<RouteParam, string>> | null
  ): string {
    const segments = RouteLink[path];

    return segments
      .map((seg) => {
        if (this.isRouteParam(seg)) {
          const val = params?.[seg];
          if (val == null) {
            throw new Error(`The missing param '${seg}' for '${path}'`);
          }
          return val;
        }
        return seg;
      })
      .join('/');
  }

  isRouteParam(data: RouteData): data is RouteParam {
    const params = Object.values(RouteParam) as string[];
    return params.includes(data as string);
  }
}
