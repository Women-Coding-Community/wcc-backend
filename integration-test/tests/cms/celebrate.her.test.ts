import { expect, test } from '@playwright/test';
import { validateSchema } from '@utils/helpers/schema.validation';
import { celebrateHerPageSchema } from '@utils/datafactory/schemas/celebrateHer.overview.schema';
import { celebrateHerData } from '@utils/datafactory/test-data/celebrate.her.page.data';
import { PATHS } from '@utils/datafactory/paths.data';
import { createOrUpdatePage } from '@utils/helpers/preconditions';

test.describe('Validate positive test cases for Celebrate Her Page API', () => {
  test.beforeEach(async ({ request }) => {
    const url = `${PATHS.PLATFORM_PAGE}?pageType=CELEBRATE_HER`;
    await createOrUpdatePage(request, 'CELEBRATE HER Page', url, celebrateHerData);
  });

  test('GET /api/cms/v1/celebrateHer/overview returns correct about us data', async ({ request }) => {
    const response = await request.get(PATHS.CELEBRATE_HER_PAGE);

    // response status validation
    expect(response.status()).toBe(200);

    const body = await response.json();

    // schema validation
    try {
      validateSchema(celebrateHerPageSchema, body);
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
      const response = await request.get(PATHS.CELEBRATE_HER_PAGE, {
        headers: headers,
      });
      expect(response.status()).toBe(401);
    });
  });
});
