import { randomUUID } from 'crypto';
import type { Page } from '@playwright/test';

export interface MockTeam {
  id: string;
  name: string;
}

export interface MockMatch {
  matchId: string;
  home: MockTeam;
  away: MockTeam;
  odds: { homeWin: number; draw: number; awayWin: number };
}

export interface MockPool {
  id: string;
  scheduledStartTime: string;
  matches: MockMatch[];
}

function mockTeam(name: string): MockTeam {
  return { id: randomUUID(), name };
}

function mockMatch(
  homeName: string,
  awayName: string,
  odds: MockMatch['odds'],
): MockMatch {
  return {
    matchId: randomUUID(),
    home: mockTeam(homeName),
    away: mockTeam(awayName),
    odds,
  };
}

export function buildMockPool(matchCount: 1 | 2 = 1): MockPool {
  const scheduledStartTime = new Date(Date.now() + 2 * 60 * 60 * 1000).toISOString();

  const matches: MockMatch[] = [
    mockMatch('E2E Lechia', 'E2E Pogoń', { homeWin: 1.85, draw: 3.4, awayWin: 4.2 }),
  ];

  if (matchCount === 2) {
    matches.push(
      mockMatch('E2E Wisła', 'E2E Cracovia', { homeWin: 2.1, draw: 3.1, awayWin: 3.3 }),
    );
  }

  return { id: randomUUID(), scheduledStartTime, matches };
}

export async function mockFuturePool(page: Page, pool: MockPool): Promise<void> {
  await page.route('**/api/match-pool/future', (route) =>
    route.fulfill({
      json: [
        {
          id: pool.id,
          status: 'SCHEDULED',
          scheduledStartTime: pool.scheduledStartTime,
          matches: pool.matches.map((match) => ({
            matchId: match.matchId,
            homeTeamId: match.home.id,
            awayTeamId: match.away.id,
            odds: {
              id: randomUUID(),
              homeWin: match.odds.homeWin,
              draw: match.odds.draw,
              awayWin: match.odds.awayWin,
            },
          })),
        },
      ],
    }),
  );

  const teamsById = new Map<string, MockTeam>();
  pool.matches.forEach((match) => {
    teamsById.set(match.home.id, match.home);
    teamsById.set(match.away.id, match.away);
  });

  await page.route('**/api/teams/public/*', (route) => {
    const teamId = new URL(route.request().url()).pathname.split('/').pop();
    const team = teamId ? teamsById.get(teamId) : undefined;

    if (!team) {
      return route.continue();
    }

    return route.fulfill({ json: { id: team.id, name: team.name } });
  });
}
