import { expect, test } from '@playwright/test';
import { validateSchema } from '@utils/helpers/schema.validation';
import { teamSchema } from '@utils/datafactory/schemas/team.schema';
test('GET /api/cms/v1/team returns correct data', async ({ request }) => {
  const response = await request.get('/api/cms/v1/team');
  expect(response.status()).toBe(200);
  const body = await response.json();
  // schema validation
  try {
    validateSchema(teamSchema, body);
  } catch (e: unknown) {
    if (e instanceof Error) {
      throw new Error(`Schema validation failed: ${e.message}`);
    } else {
      throw new Error('Schema validation failed with an unknown error');
    }
  }
});
