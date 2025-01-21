import { expect, test } from '@playwright/test';
import { collaboratorsSchema } from '@utils/datafactory/schemas/collaborators.schema';
import { validateSchema } from '@utils/helpers/schema.validation';

test('GET /api/cms/v1/collaborators returns correct data', async ({ request }) => {
  const response = await request.get('/api/cms/v1/collaborators');
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
