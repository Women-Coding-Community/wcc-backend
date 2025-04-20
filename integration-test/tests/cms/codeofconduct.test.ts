import { expect, test } from '@playwright/test';
import { validateSchema } from '@utils/helpers/schema.validation';
import { codeofconductSchema } from '@utils/datafactory/schemas/codeofconduct.schema';
import { codeOfConductPageData } from '@utils/datafactory/test-data/codeofconduct.page.data';
import { PATHS } from '@utils/datafactory/paths.data';

test.describe('Validate positive test cases for Code Of Conduct Page API', () => {
  test.beforeEach(async ({ request }) => {
    console.log(`Creating Code Of Conduct Page`);
    const createPageResponse = await request.post('/api/platform/v1/page?pageType=CODE_OF_CONDUCT', {
      data: codeOfConductPageData,
    });
    console.log(`Sending POST request to: ${createPageResponse.url()}`);
    console.log(`Response Status: ${createPageResponse.status()}`);
    console.log('Response Body:', JSON.stringify(createPageResponse.json()));

    if (createPageResponse.status() == 409) {
      console.log(`Updating Code Of Conduct Page`);
      const updatePageResponse = await request.put('/api/platform/v1/page?pageType=CODE_OF_CONDUCT', {
        data: codeOfConductPageData,
      });
      console.log(`Sending PUT request to: ${updatePageResponse.url()}`);
      console.log(`Response Status: ${updatePageResponse.status()}`);
      console.log('Response Body:', JSON.stringify(updatePageResponse.json()));
    }
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
