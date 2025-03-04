import { expect, test } from '@playwright/test';
import { eventsSchema } from '@utils/datafactory/schemas/events.schema';
import { validateSchema } from '@utils/helpers/schema.validation';

test('GET /api/cms/v1/events returns success response code', async ({ request }) => {
  const response = await request.get(`/api/cms/v1/events`);
  expect(response.status()).toBe(200);
  const body = await response.json();
  // schema validation
  try {
    validateSchema(eventsSchema, body);
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
      const response = await request.get(`/api/cms/v1/events`, {
        headers: headers,
      });
      expect(response.status()).toBe(401);
    });
  });
});
