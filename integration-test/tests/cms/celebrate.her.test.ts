import { expect, test } from '@playwright/test';
import { validateSchema } from '@utils/helpers/schema.validation';
import { celebrateHerPageSchema } from '@utils/datafactory/schemas/celebrateHer.overview.schema';
import { celebrateHerData } from '@utils/datafactory/test-data/celebrate.her.page.data';

test.describe('Validate positive test cases for Celebrate Her Page API', () => {
  test.beforeEach(async ({ request }) => {
    console.log(`Creating Celebrate Her Page`);
    const createPageResponse = await request.post('/api/platform/v1/page?pageType=CELEBRATE_HER', {
      data: celebrateHerData,
    });
    console.log(`Sending POST request to: ${createPageResponse.url()}`);
    console.log(`Response Status: ${createPageResponse.status()}`);
    console.log('Response Body:', JSON.stringify(createPageResponse.json()));

    if (createPageResponse.status() == 409) {
      console.log(`Updating FOOTER Page`);
      const updateFooterPageResponse = await request.put('/api/platform/v1/page?pageType=CELEBRATE_HER', {
        data: celebrateHerData,
      });
      console.log(`Sending PUT request to: ${updateFooterPageResponse.url()}`);
      console.log(`Response Status: ${updateFooterPageResponse.status()}`);
      console.log('Response Body:', JSON.stringify(updateFooterPageResponse.json()));
    }
  });

  test('GET /api/cms/v1/celebrateHer/overview returns correct about us data', async ({ request }) => {
    const response = await request.get(`/api/cms/v1/celebrateHer`);

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
      const response = await request.get(`/api/cms/v1/footer`, {
        headers: headers,
      });
      expect(response.status()).toBe(401);
    });
  });
});
