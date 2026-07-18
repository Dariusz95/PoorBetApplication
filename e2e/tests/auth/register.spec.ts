import { expect, test } from "../../fixtures/pages.fixture";
import { createTestUser } from "../../support/test-user";

test.describe("Registration", () => {
  test("a new user registers via the form and lands on /auth/login", async ({
    page,
    registerPage,
  }) => {
    const user = createTestUser();

    await registerPage.goto();
    await registerPage.register(user.email, user.password);

    await expect(page).toHaveURL("/auth/login");
  });
});
