import { expect, test } from '@playwright/test';
import { codeofconductExepctedInformation } from '@utils/datafactory/codeofconduct.data';
import { validateSchema } from '@utils/helpers/schema.validation';
import { codeofconductSchema } from '@utils/datafactory/schemas/codeofconduct.schema';

test('GET /api/cms/v1/code-of-conduct returns correct data', async ({ request }) => {
  const response = await request.get(`/api/cms/v1/code-of-conduct`);

  expect(response.status()).toBe(200);

  const body = await response.json();

  // schema validation

  try {
    validateSchema(codeofconductSchema, body);
  } catch (e: unknown) {
    if (e instanceof Error) {
      throw new Error(`Schema validation failed: ${e.message}`);
    } else {
      throw new Error('Schema validation failed with an unknown error');
    }
  }

  //response body validation
  expect(body).toEqual(codeofconductExepctedInformation);
});

test.describe('unauthorized request with invalid headers', () => {
  const testData = [
    { description: 'header is empty', headers: { 'X-API-KEY': '' } },
    { description: 'header is invalid', headers: { 'X-API-KEY': 'invalid_key' } },
  ];

  testData.forEach(({ description, headers }) => {
    test(`${description}`, async ({ request }) => {
      const response = await request.get(`/api/cms/v1/code-of-conduct`, {
        headers: headers,
      });
      expect(response.status()).toBe(401);
    });
  });
});
