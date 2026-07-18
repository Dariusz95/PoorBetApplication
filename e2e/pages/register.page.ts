import type { Page } from '@playwright/test';

import { BasePage } from './base.page';

export class RegisterPage extends BasePage {
  readonly emailInput = this.page.getByTestId('register-email-input');
  readonly passwordInput = this.page.getByTestId('register-password-input');
  readonly confirmPasswordInput = this.page.getByTestId('register-confirm-password-input');
  readonly submitButton = this.page.getByTestId('register-submit-button');
  readonly loginLink = this.page.getByTestId('register-login-link');

  constructor(page: Page) {
    super(page);
  }

  async goto(): Promise<void> {
    await this.page.goto('/auth/register');
  }

  async register(email: string, password: string, confirmPassword = password): Promise<void> {
    await this.emailInput.fill(email);
    await this.passwordInput.fill(password);
    await this.confirmPasswordInput.fill(confirmPassword);
    await this.submitButton.click();
  }
}
