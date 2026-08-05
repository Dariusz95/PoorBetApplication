import type { Locator, Page } from '@playwright/test';
import { BasePage } from './base.page';

export type BetOutcome = 'home-win' | 'draw' | 'away-win';

export class BetPage extends BasePage {
  constructor(page: Page) {
    super(page);
  }

  async goto(): Promise<void> {
    await this.page.goto('/app');
  }

  futureTab(poolId: string): Locator {
    return this.page.getByTestId(`bet-tab-${poolId}`);
  }

  async openFutureTab(poolId: string): Promise<void> {
    await this.futureTab(poolId).click();
  }

  matchCard(matchId: string): Locator {
    return this.page.getByTestId(`pool-match-${matchId}`);
  }

  oddsButton(matchId: string, outcome: BetOutcome): Locator {
    return this.page.getByTestId(`match-${matchId}-${outcome}`);
  }

  async selectOdds(matchId: string, outcome: BetOutcome): Promise<void> {
    await this.oddsButton(matchId, outcome).click();
  }
}
