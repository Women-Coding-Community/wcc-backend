import { expect, test } from '@playwright/test';
import { validateSchema } from '@utils/helpers/schema.validation';
import { landingPageSchema } from '@utils/datafactory/schemas/landing.page.schema';

test('GET /api/cms/v1/landingPage returns correct landingPage data', async ({ request }) => {
  const response = await request.get(`/api/cms/v1/landingPage`);

  // response status validation
  expect(response.status()).toBe(200);

  const body = await response.json();

  // schema validation
  try {
    validateSchema(landingPageSchema, body);
  } catch (e: unknown) {
    if (e instanceof Error) {
      throw new Error(`Schema validation failed: ${e.message}`);
    } else {
      throw new Error('Schema validation failed with an unknown error');
    }
  }
});

test.describe('unauthorized request with invalid headers', () => {
  const testData = [
    { description: 'header is empty', headers: { 'X-API-KEY': '' } },
    { description: 'header is invalid', headers: { 'X-API-KEY': 'invalid_key' } },
  ];

  testData.forEach(({ description, headers }) => {
    test(`${description}`, async ({ request }) => {
      const response = await request.get(`/api/cms/v1/landingPage`, {
        headers: headers,
      });
      expect(response.status()).toBe(401);
    });
  });
});
