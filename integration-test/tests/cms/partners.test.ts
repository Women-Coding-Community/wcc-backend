import { expect, test } from '@playwright/test';
import { validateSchema } from '@utils/helpers/schema.validation';
import { partnersSchema } from '@utils/datafactory/schemas/partners.schema';


test.describe('Validate positive test cases for MENTORSHIPCODEOFCONDUCT Page API', () => {
  test.beforeAll(async ({ request }) => {
    console.log(`Creating PARTNERS Page`);
    const createPageResponse = await request.post('/api/platform/v1/page?pageType=PARTNERS', {
      
    });
    console.log(`Sending POST request to: ${createPageResponse.url()}`);
    console.log(`Response Status: ${createPageResponse.status()}`);
    console.log('Response Body:', JSON.stringify(createPageResponse.json()));

    if (createPageResponse.status() == 409) {
      console.log(`Updating PARTNERS Page`);
      const updatePartnersPageResponse = await request.put('/api/platform/v1/page?pageType=PARTNERS', {
        
      });
      console.log(`Sending PUT request to: ${updatePartnersPageResponse.url()}`);
      console.log(`Response Status: ${updatePartnersPageResponse.status()}`);
      console.log('Response Body:', JSON.stringify(updatePartnersPageResponse.json()));
    }
  });
test('GET /api/cms/v1/partners returns correct data', async ({ request }) => {
  const response = await request.get('/api/cms/v1/partners');
  expect(response.status()).toBe(200);
  // response status validation
   const body = await response.json();

  // schema validation
  try {
    validateSchema(partnersSchema, body);
  } catch (e: unknown) {
    if (e instanceof Error) {
      throw new Error(`Schema validation failed: ${e.message}`);
    } else {
      throw new Error('Schema validation failed with an unknown error');
    }
  }
});
test.afterAll(async ({ request }) => {
  console.log(`Deleting PARTNERS Page`);
  const deletePartnersPageResponse = await request.delete('/api/platform/v1/page?id=page%3APARTNERS');
  console.log(`Sending PUT request to: ${deletePartnersPageResponse.status()}`);
});
});
test.describe('unauthorized request with invalid headers', () => {
  const testData = [
    { description: 'header is empty', headers: { 'X-API-KEY': '' } },
    { description: 'header is invalid', headers: { 'X-API-KEY': 'invalid_key' } },
  ];

  testData.forEach(({ description, headers }) => {
    test(`${description}`, async ({ request }) => {
      const response = await request.get(`/api/cms/v1/partners`, {
        headers: headers,
      });
      expect(response.status()).toBe(401);
    });
  });
});
