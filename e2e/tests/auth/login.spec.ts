import { expect, test } from "../../fixtures/pages.fixture";
import { SETUP_USER } from "../../support/test-user";

test.describe("Login", () => {
  test("a registered user logs in via the form and lands on /app", async ({
    page,
    loginPage,
  }) => {
    await loginPage.goto();
    await loginPage.login(SETUP_USER.email, SETUP_USER.password);

    await expect(page).toHaveURL("/app");
  });
});
