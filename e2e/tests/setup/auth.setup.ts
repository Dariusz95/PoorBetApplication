import { test as setup, expect } from '../../fixtures/pages.fixture';
import { authFile } from '../../support/auth-storage';
import { SETUP_USER } from '../../support/test-user';

setup('authenticate as SETUP_USER', async ({ page, loginPage }) => {
  await loginPage.goto();
  await loginPage.login(SETUP_USER.email, SETUP_USER.password);

  await expect(page).toHaveURL('/app');

  await page.context().storageState({ path: authFile('user') });
});
