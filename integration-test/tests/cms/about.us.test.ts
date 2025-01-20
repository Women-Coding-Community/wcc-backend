import { expect, test } from '@playwright/test';
import { validateSchema } from '@utils/helpers/schema.validation';
import { aboutSchema } from '@utils/datafactory/schemas/about.schema';

test('GET /api/cms/v1/about returns correct about us data', async ({ request }) => {
  const response = await request.get(`/api/cms/v1/about`);

  // response status validation
  expect(response.status()).toBe(200);

  const body = await response.json();

  // schema validation
  try {
    validateSchema(aboutSchema, body);
  } catch (e: unknown) {
    if (e instanceof Error) {
      throw new Error(`Schema validation failed: ${e.message}`);
    } else {
      throw new Error('Schema validation failed with an unknown error');
    }
  }
  
});