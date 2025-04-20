import { expect, test } from '@playwright/test';
import { validateSchema } from '@utils/helpers/schema.validation';
import { codeofconductSchema } from '@utils/datafactory/schemas/codeofconduct.schema';
import { codeOfConductPageData } from '@utils/datafactory/test-data/codeofconduct.page.data';
import { PATHS } from '@utils/datafactory/paths.data';
import { createOrUpdatePage } from '@utils/helpers/preconditions';

test.describe('Validate positive test cases for Code Of Conduct Page API', () => {
  test.beforeEach(async ({ request }) => {
    const url = '/api/platform/v1/page?pageType=CODE_OF_CONDUCT';
    await createOrUpdatePage(request, 'CODE OF CONDUCT Page', url, codeOfConductPageData);
  });

  test('GET /api/cms/v1/code-of-conduct returns correct data', async ({ request }) => {
    const response = await request.get(PATHS.CODE_OF_CONDUCT);

    // response status validation
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
  });
});

test.describe('unauthorized request with invalid headers', () => {
  const testData = [
    { description: 'header is empty', headers: { 'X-API-KEY': '' } },
    { description: 'header is invalid', headers: { 'X-API-KEY': 'invalid_key' } },
  ];

  testData.forEach(({ description, headers }) => {
    test(`${description}`, async ({ request }) => {
      const response = await request.get(PATHS.CODE_OF_CONDUCT, {
        headers: headers,
      });
      expect(response.status()).toBe(401);
    });
  });
});
