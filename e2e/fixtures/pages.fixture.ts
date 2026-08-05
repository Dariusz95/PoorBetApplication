import { test as base } from '@playwright/test';

import { BetPage } from '../pages/bet.page';
import { CouponPage } from '../pages/coupon.page';
import { LoginPage } from '../pages/login.page';
import { RegisterPage } from '../pages/register.page';

interface PageFixtures {
  loginPage: LoginPage;
  registerPage: RegisterPage;
  betPage: BetPage;
  couponPage: CouponPage;
}

export const test = base.extend<PageFixtures>({
  loginPage: async ({ page }, use) => {
    await use(new LoginPage(page));
  },
  registerPage: async ({ page }, use) => {
    await use(new RegisterPage(page));
  },
  betPage: async ({ page }, use) => {
    await use(new BetPage(page));
  },
  couponPage: async ({ page }, use) => {
    await use(new CouponPage(page));
  },
});

export { expect } from '@playwright/test';
