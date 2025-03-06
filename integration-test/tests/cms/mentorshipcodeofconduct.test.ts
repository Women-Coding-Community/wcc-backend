import { expect, test } from '@playwright/test';
import { validateSchema } from '@utils/helpers/schema.validation';
import { mentorshipcodeofconductSchema } from '@utils/datafactory/schemas/mentorshipcodeofconduct.schema';


test.describe('Validate positive test cases for MENTORSHIPCODEOFCONDUCT Page API', () => {
  test.beforeEach(async ({ request }) => {
    console.log(`Creating MENTORSHIPCODEOFCONDUCT Page`);
    const createPageResponse = await request.post('/api/platform/v1/page?pageType=MENTORSHIPCODEOFCONDUCT', {
      
    });
    console.log(`Sending POST request to: ${createPageResponse.url()}`);
    console.log(`Response Status: ${createPageResponse.status()}`);
    console.log('Response Body:', JSON.stringify(createPageResponse.json()));

    if (createPageResponse.status() == 409) {
      console.log(`Updating MENTORSHIPCODEOFCONDUCT Page`);
      const updateMentorshipCodeofConductPageResponse = await request.put('/api/platform/v1/page?pageType=MENTORSHIPCODEOFCONDUCT', {
        
      });
      console.log(`Sending PUT request to: ${updateMentorshipCodeofConductPageResponse.url()}`);
      console.log(`Response Status: ${updateMentorshipCodeofConductPageResponse.status()}`);
      console.log('Response Body:', JSON.stringify(updateMentorshipCodeofConductPageResponse.json()));
    }
  });
  test('GET /api/cms/v1/mentorship/code-of-conduct returns correct data', async ({ request }) => {
  const response = await request.get('/api/cms/v1/mentorship/code-of-conduct');
  expect(response.status()).toBe(200);
  // response status validation
  const body = await response.json();

  // schema validation
  try {
    validateSchema(mentorshipcodeofconductSchema, body);
  } catch (e: unknown) {
    if (e instanceof Error) {
      throw new Error(`Schema validation failed: ${e.message}`);
    } else {
      throw new Error('Schema validation failed with an unknown error');
    }
  }
});
test.afterEach(async ({ request }) => {
  console.log(`Deleting MENTORSHIPCODEOFCONDCUT Page`);
  const deleteMentorshipcodeofconductPageResponse = await request.delete('/api/platform/v1/page?id=page%3AMENTORSHIPCODEOFCONDUCT');
  console.log(`Sending PUT request to: ${deleteMentorshipcodeofconductPageResponse.url()}`);
  console.log(`Responsementorship Status: ${deleteMentorshipcodeofconductPageResponse.status()}`);
});
});
test.describe('unauthorized request with invalid headers', () => {
  const testData = [
    { description: 'header is empty', headers: { 'X-API-KEY': '' } },
    { description: 'header is invalid', headers: { 'X-API-KEY': 'invalid_key' } },
  ];

  testData.forEach(({ description, headers }) => {
    test(`${description}`, async ({ request }) => {
      const response = await request.get(`/api/cms/v1/mentorship/code-of-conduct`, {
        headers: headers,
      });
      expect(response.status()).toBe(401);
    });
  });
});
