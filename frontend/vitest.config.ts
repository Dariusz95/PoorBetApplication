import { defineConfig } from 'vitest/config';

export default defineConfig({
  test: {
    globals: true,
    environment: 'jsdom',
    setupFiles: ['src/test-setup.ts'],
    include: ['src/**/*.spec.ts'],
  },
  resolve: {
    alias: {
      '@app': '/src/app',
      '@core': '/src/app/core',
      '@shared': '/src/app/shared',
      '@features': '/src/app/features',
      '@env': '/src/environments',
    },
  },
});
