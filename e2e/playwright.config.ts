import path from 'path';

import { defineConfig, devices } from '@playwright/test';
import dotenv from 'dotenv';

import { authFile } from './support/auth-storage';

dotenv.config({
  path: path.resolve(__dirname, `.env.e2e.${process.env['E2E_ENV'] ?? 'local'}`),
});

export default defineConfig({
  testDir: '.',
  outputDir: './test-results',
  fullyParallel: true,
  forbidOnly: !!process.env['CI'],
  retries: process.env['CI'] ? 2 : 0,
  reporter: [['list'], ['html', { outputFolder: 'playwright-report', open: 'never' }]],
  timeout: 45_000,
  expect: {
    timeout: 10_000,
  },

  use: {
    baseURL: process.env['E2E_BASE_URL'] ?? 'http://localhost:4200',
    trace: 'on-first-retry',
    screenshot: 'only-on-failure',
    video: 'retain-on-failure',
    actionTimeout: 20_000,
  },

  projects: [
    {
      name: 'setup',
      testMatch: /auth\.setup\.ts/,
      use: { ...devices['Desktop Chrome'] },
    },
    {
      name: 'anonymous',
      testMatch: [/tests\/auth\/.*/, /tests\/public\/.*/],
      use: { ...devices['Desktop Chrome'] },
    },
    {
      name: 'authenticated',
      testIgnore: [/tests\/auth\/.*/, /tests\/public\/.*/],
      dependencies: ['setup'],
      use: {
        ...devices['Desktop Chrome'],
        storageState: authFile('user'),
      },
    },
  ],
});
