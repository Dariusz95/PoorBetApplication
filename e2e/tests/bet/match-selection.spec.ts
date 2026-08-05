import { expect, test } from '../../fixtures/pages.fixture';
import { buildMockPool, mockFuturePool } from '../../support/mock-matches';

test.describe('Match selection and coupon', () => {
  test('selecting odds adds the match to the coupon', async ({ betPage, couponPage, page }) => {
    const pool = buildMockPool(1);
    const [match] = pool.matches;
    await mockFuturePool(page, pool);

    await betPage.goto();
    await betPage.openFutureTab(pool.id);
    await betPage.selectOdds(match.matchId, 'home-win');

    await expect(couponPage.selectedCount).toHaveText('1');
    await expect(couponPage.item(match.matchId)).toBeVisible();
    await expect(couponPage.item(match.matchId)).toContainText(
      `${match.home.name} vs ${match.away.name}`,
    );
    await expect(couponPage.item(match.matchId)).toContainText(
      match.odds.homeWin.toFixed(2),
    );
  });

  test('clicking the same odds again removes the bet from the coupon', async ({
    betPage,
    couponPage,
    page,
  }) => {
    const pool = buildMockPool(1);
    const [match] = pool.matches;
    await mockFuturePool(page, pool);

    await betPage.goto();
    await betPage.openFutureTab(pool.id);
    await betPage.selectOdds(match.matchId, 'home-win');
    await expect(couponPage.item(match.matchId)).toBeVisible();

    await betPage.selectOdds(match.matchId, 'home-win');

    await expect(couponPage.selectedCount).toHaveText('0');
    await expect(couponPage.emptyState).toBeVisible();
  });

  test('selecting a different outcome for the same match replaces the previous bet', async ({
    betPage,
    couponPage,
    page,
  }) => {
    const pool = buildMockPool(1);
    const [match] = pool.matches;
    await mockFuturePool(page, pool);

    await betPage.goto();
    await betPage.openFutureTab(pool.id);
    await betPage.selectOdds(match.matchId, 'home-win');
    await betPage.selectOdds(match.matchId, 'draw');

    await expect(couponPage.selectedCount).toHaveText('1');
    await expect(couponPage.item(match.matchId)).toContainText(
      match.odds.draw.toFixed(2),
    );
  });

  test('selecting odds for two different matches adds both to the coupon', async ({
    betPage,
    couponPage,
    page,
  }) => {
    const pool = buildMockPool(2);
    const [firstMatch, secondMatch] = pool.matches;
    await mockFuturePool(page, pool);

    await betPage.goto();
    await betPage.openFutureTab(pool.id);
    await betPage.selectOdds(firstMatch.matchId, 'home-win');
    await betPage.selectOdds(secondMatch.matchId, 'away-win');

    await expect(couponPage.selectedCount).toHaveText('2');
    await expect(couponPage.item(firstMatch.matchId)).toBeVisible();
    await expect(couponPage.item(secondMatch.matchId)).toBeVisible();
  });
});
