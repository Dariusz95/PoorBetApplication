import type { Page } from '@playwright/test';

import { BasePage } from './base.page';

export class LoginPage extends BasePage {
  readonly emailInput = this.page.getByTestId('login-email-input');
  readonly passwordInput = this.page.getByTestId('login-password-input');
  readonly submitButton = this.page.getByTestId('login-submit-button');
  readonly testUserButton = this.page.getByTestId('login-test-user-button');
  readonly registerLink = this.page.getByTestId('login-register-link');

  constructor(page: Page) {
    super(page);
  }

  async goto(): Promise<void> {
    await this.page.goto('/auth/login');
  }

  async login(email: string, password: string): Promise<void> {
    await this.emailInput.fill(email);
    await this.passwordInput.fill(password);
    await this.submitButton.click();
  }
}
