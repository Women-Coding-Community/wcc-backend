import {defineConfig, devices} from '@playwright/test';

export default defineConfig({
  globalSetup: require.resolve('./global-setup'),
  projects: [
    {
      name: 'chrome',
      use: { ...devices['Desktop Chrome'] },
    },
  ],
});