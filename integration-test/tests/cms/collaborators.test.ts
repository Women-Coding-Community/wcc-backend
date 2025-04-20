import { expect, test } from '@playwright/test';
import { PATHS } from '@utils/datafactory/paths.data';
import { collaboratorsSchema } from '@utils/datafactory/schemas/collaborators.schema';
import { validateSchema } from '@utils/helpers/schema.validation';

test('GET /api/cms/v1/collaborators returns correct data', async ({ request }) => {
  const response = await request.get(PATHS.COLLABORATORS);
  expect(response.status()).toBe(200);
  const body = await response.json();
  // schema validation
  try {
    validateSchema(collaboratorsSchema, body);
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
      const response = await request.get(PATHS.COLLABORATORS, {
        headers: headers,
      });
      expect(response.status()).toBe(401);
    });
  });
});
