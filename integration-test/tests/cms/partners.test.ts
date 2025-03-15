import { expect, test } from '@playwright/test';
import { validateSchema } from '@utils/helpers/schema.validation';
import { partnersSchema } from '@utils/datafactory/schemas/partners.schema';

test('GET /api/cms/v1/partners returns correct data', async ({ request }) => {
  const response = await request.get('/api/cms/v1/partners');
  expect(response.status()).toBe(200);
  // response status validation
   const body = await response.json();

  // schema validation
  try {
    validateSchema(partnersSchema, body);
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
      const response = await request.get(`/api/cms/v1/partners`, {
        headers: headers,
      });
      expect(response.status()).toBe(401);
    });
  });
});
