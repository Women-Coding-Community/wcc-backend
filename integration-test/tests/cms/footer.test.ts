import { expect, test } from '@playwright/test';
import { validateSchema } from '@utils/helpers/schema.validation';
import { footerSchema } from '@utils/datafactory/schemas/footer.schema';
import { footerData } from '@utils/datafactory/test-data/footer.page';

test.describe('Validate positive test cases for FOOTER Page API', () => {
  test.beforeEach(async ({ request }) => {
    console.log(`Creating FOOTER Page`);
    const createPageResponse = await request.post('/api/platform/v1/page?pageType=FOOTER', {
      data: footerData,
    });
    console.log(`Sending POST request to: ${createPageResponse.url()}`);
    console.log(`Response Status: ${createPageResponse.status()}`);
    console.log('Response Body:', JSON.stringify(createPageResponse.json()));

    if (createPageResponse.status() == 409) {
      console.log(`Updating FOOTER Page`);
      const updateFooterPageResponse = await request.put('/api/platform/v1/page?pageType=FOOTER', {
        data: footerData,
      });
      console.log(`Sending PUT request to: ${updateFooterPageResponse.url()}`);
      console.log(`Response Status: ${updateFooterPageResponse.status()}`);
      console.log('Response Body:', JSON.stringify(updateFooterPageResponse.json()));
    }
  });

  test('GET /api/cms/v1/footer returns correct footer data', async ({ request }) => {
    const response = await request.get(`/api/cms/v1/footer`);

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

  test.afterEach(async ({ request }) => {
    console.log(`Deleting FOOTER Page`);
    const deleteFooterPageResponse = await request.delete('/api/platform/v1/page?id=page%3AFOOTER');
    console.log(`Sending PUT request to: ${deleteFooterPageResponse.url()}`);
    console.log(`Response Status: ${deleteFooterPageResponse.status()}`);
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
