import { expect, test } from '@playwright/test';
import { validateSchema } from '@utils/helpers/schema.validation';
import { mentorshipSchema } from '@utils/datafactory/schemas/mentorship.schema';

test('GET /api/cms/v1/mentorship/overview returns correct data', async ({ request }) => {
  const response = await request.get('/api/cms/v1/mentorship/overview');
  expect(response.status()).toBe(200);
  // response status validation
  expect(response.status()).toBe(200);

  const body = await response.json();
  
  // schema validation
  try {
  validateSchema(mentorshipSchema, body);
  } catch (e: unknown) {
    if (e instanceof Error) {
      throw new Error(`Schema validation failed: ${e.message}`);
    } else {
      throw new Error('Schema validation failed with an unknown error');
    }
  }
});  
