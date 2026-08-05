import type { Locator, Page } from '@playwright/test';
import { BasePage } from './base.page';

export class CouponPage extends BasePage {
  readonly card = this.page.getByTestId('coupon-card');
  readonly selectedCount = this.card.getByTestId('coupon-count');
  readonly emptyState = this.card.getByTestId('coupon-empty');

  constructor(page: Page) {
    super(page);
  }

  async goto(): Promise<void> {
    await this.page.goto('/app');
  }

  item(matchId: string): Locator {
    return this.card.getByTestId(`coupon-item-${matchId}`);
  }
}
