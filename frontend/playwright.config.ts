import { defineConfig } from '@playwright/test';
import path from 'node:path';

export default defineConfig({
  testDir: './e2e',
  testMatch: '**/*.e2e.spec.ts',
  fullyParallel: false,
  workers: 1,
  retries: 0,
  timeout: 30000,
  use: {
    baseURL: 'http://localhost:4200',
    trace: 'retain-on-failure'
  },
  webServer: [
    {
      command: '.\\mvnw.cmd spring-boot:run',
      cwd: path.join(__dirname, '../backend'),
      url: 'http://localhost:8080/api/guests',
      reuseExistingServer: true,
      timeout: 180000
    },
    {
      command: 'npm start',
      cwd: __dirname,
      url: 'http://localhost:4200',
      reuseExistingServer: true,
      timeout: 120000
    }
  ]
});
