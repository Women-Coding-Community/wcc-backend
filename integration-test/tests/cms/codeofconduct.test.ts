import { expect, test } from '@playwright/test';
import { codeofconductExepctedInformation } from '@utils/datafactory/codeofconduct.data';
import { validateSchema } from '@utils/helpers/schema.validation';
import { codeofconductSchema } from '@utils/datafactory/schemas/codeofconduct.schema';

test('GET /api/cms/v1/code-of-conduct returns correct data', async ({ request }) => {
  const response = await request.get(`/api/cms/v1/code-of-conduct`);

  expect(response.status()).toBe(200);
  const body = await response.json();

  const expectedResponse = codeofconductExepctedInformation;

  expect(body).toEqual(expectedResponse);

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

  // response body validation
  expect(body).toEqual(codeofconductExepctedInformation);
});

