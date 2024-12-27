import { expect, test } from '@playwright/test';

test('GET /swagger-ui/index.html', async ({ request }) => {
  const response = await request.get(`/swagger-ui/index.html`);

  expect(response.status()).toBe(200);
});
