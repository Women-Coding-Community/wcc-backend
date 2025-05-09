import { expect, test } from '@playwright/test';
import { validateSchema } from '@utils/helpers/schema.validation';
import { mentorshipfaqSchema } from '@utils/datafactory/schemas/mentorshipfaq.schema';
import { PATHS } from '@utils/datafactory/paths.data';
import { createOrUpdatePage } from '@utils/helpers/preconditions';
import { mentorshipFaqData } from '@utils/datafactory/test-data/mentorship.faq.page.data';

test.describe('Validate positive test cases for MENTORSHIPFAQ Page API', () => {
    test.beforeEach(async ({ request }) => {
      const url = `${PATHS.PLATFORM_PAGE}?pageType=MENTORSHIP_FAQ`;
      await createOrUpdatePage(request, 'MENTORSHIP FAQ Page', url, mentorshipFaqData);
    });

  test('GET /api/cms/v1/mentorship/faq returns correct data', async ({ request }) => {
    const response = await request.get(PATHS.MENTORSHIP_FAQ);
    expect(response.status()).toBe(200);
    // response status validation
    const body = await response.json();
  
    // schema validation
    try {
      validateSchema(mentorshipfaqSchema, body);
    } catch (e: unknown) {
      if (e instanceof Error) {
        throw new Error(`Schema validation failed: ${e.message}`);
      } else {
        throw new Error('Schema validation failed with an unknown error');
      }
    }
  });
});

test.describe('unauthorized request with invalid headers', () => {
  const testData = [
    { description: 'header is empty', headers: { 'X-API-KEY': '' } },
    { description: 'header is invalid', headers: { 'X-API-KEY': 'invalid_key' } },
  ];

  testData.forEach(({ description, headers }) => {
    test(`${description}`, async ({ request }) => {
      const response = await request.get(PATHS.MENTORSHIP_FAQ, {
        headers: headers,
      });
      expect(response.status()).toBe(401);
    });
  });
});
