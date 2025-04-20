import { expect, test } from '@playwright/test';
import { validateSchema } from '@utils/helpers/schema.validation';
import { footerSchema } from '@utils/datafactory/schemas/footer.schema';
import { footerData } from '@utils/datafactory/test-data/footer.page';
import { PATHS } from '@utils/datafactory/paths.data';
import { createOrUpdatePage } from '@utils/helpers/preconditions';

test.describe('Validate positive test cases for FOOTER Page API', () => {
  test.beforeEach(async ({ request }) => {
    const url = '/api/platform/v1/page?pageType=FOOTER';
    await createOrUpdatePage(request, 'FOOTER Page', url, footerData);
  });

  test('GET /api/cms/v1/footer returns correct footer data', async ({ request }) => {
    const response = await request.get(PATHS.FOOTER_PAGE);

    // response status validation
    expect(response.status()).toBe(200);

    const body = await response.json();

    // schema validation
    try {
      validateSchema(footerSchema, body);
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
      const response = await request.get(PATHS.FOOTER_PAGE, {
        headers: headers,
      });
      expect(response.status()).toBe(401);
    });
  });
});
