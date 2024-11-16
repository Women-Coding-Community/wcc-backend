import {expect, request, test} from '@playwright/test';

test('GET /api/cms/v1/landingPage returns correct data', async () => {
  const context = await request.newContext();
  const response = await context.get(`/api/cms/v1/landingPage`);

  expect(response.status()).toBe(200);

  const data = await response.json();
  expect(data.heroSection).toBeDefined();
  expect(data.fullBannerSection).toBeDefined();
  expect(data.programmes).toBeDefined();
  expect(data.events).toBeDefined();
  // TODO - Check the complete contract
});