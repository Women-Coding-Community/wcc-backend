import {defineConfig} from '@playwright/test';
import EnvVariables from "./tests/envVariables";

export default defineConfig({
  globalSetup: require.resolve('./global-setup'),
  use: {
    baseURL: EnvVariables.getApiTest(),
  },
  projects: [ { name: 'chrome' }],
});