export const RoutePath = {
  Register: 'REGISTER',
  Login: 'LOGIN',
  Home: 'HOME',
  App: 'APP',
  MyCoupons: 'MY_COUPONS',
  Ranking: 'RANKING',
} as const;

export type RoutePath = (typeof RoutePath)[keyof typeof RoutePath];
