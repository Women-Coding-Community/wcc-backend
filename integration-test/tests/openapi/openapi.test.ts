import {expect, request, test} from '@playwright/test';

test('GET /swagger-ui/index.html', async () => {
  const context = await request.newContext();

  const response = await context.get(`/swagger-ui/index.html`);

  expect(response.status()).toBe(200);
});