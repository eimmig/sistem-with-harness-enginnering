import { defineConfig } from '@playwright/test';

export default defineConfig({
  testDir: './e2e',
  testMatch: '**/*.e2e.spec.ts',
  fullyParallel: false,
  workers: 1,
  retries: 0,
  timeout: 30000,
  globalSetup: require.resolve('./e2e/global-setup.ts'),
  use: {
    baseURL: 'http://localhost:4200',
    trace: 'retain-on-failure'
  },
  webServer: [
    {
      command: 'npm start',
      cwd: __dirname,
      url: 'http://localhost:4200',
      reuseExistingServer: true,
      timeout: 120000
    }
  ]
});
