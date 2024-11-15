import {expect, request, test} from '@playwright/test';
import ApiHost from "../ApiHost";

test('GET /swagger-ui/index.html', async () => {
  const apiHost = ApiHost.get();
  const context = await request.newContext();

  const response = await context.get(`${apiHost}/swagger-ui/index.html`);

  expect(response.status()).toBe(200);
});