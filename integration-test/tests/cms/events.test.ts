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
