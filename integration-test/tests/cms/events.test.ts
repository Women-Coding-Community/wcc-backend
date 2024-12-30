import { expect, test } from '@playwright/test';

test('GET /api/cms/v1/events returns success response code', async ({ request }) => {
  const response = await request.get(`/api/cms/v1/events`);
  expect(response.status()).toBe(200);
});
