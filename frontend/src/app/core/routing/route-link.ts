import { RouteData } from './route-data';
import { RouteFragment } from './route-fragment';
import { RouteParam } from './route-param';
import { RoutePath } from './route-path';

export const RouteLink: Record<RoutePath, RouteData[]> = {
  [RoutePath.Register]: [
    RouteFragment.Slash,
    RouteFragment.Auth,
    RouteFragment.Register,
  ],
  [RoutePath.Login]: [
    RouteFragment.Slash,
    RouteFragment.Auth,
    RouteFragment.Login,
  ],
  [RoutePath.Home]: [RouteFragment.Slash, RouteParam.anyId],
};
