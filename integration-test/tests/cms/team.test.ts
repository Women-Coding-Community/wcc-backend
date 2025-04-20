import { expect, test } from '@playwright/test';
import { validateSchema } from '@utils/helpers/schema.validation';
import { teamSchema } from '@utils/datafactory/schemas/team.schema';
import { teamPageData } from '@utils/datafactory/test-data/team.page.data';
import { PATHS } from '@utils/datafactory/paths.data';
import { createOrUpdatePage } from '@utils/helpers/preconditions';

test.describe('Validate positive test cases for TEAM Page API', () => {
  test.beforeAll(async ({ request }) => {
    const url = '/api/platform/v1/page?pageType=TEAM';
    await createOrUpdatePage(request, 'TEAM Page', url, teamPageData);
  });
  test('GET /api/cms/v1/team returns correct data', async ({ request }) => {
    const response = await request.get(PATHS.TEAM_PAGE);
    expect(response.status()).toBe(200);
    const body = await response.json();
    // schema validation
    try {
      validateSchema(teamSchema, body);
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
      const response = await request.get(PATHS.TEAM_PAGE, {
        headers: headers,
      });
      expect(response.status()).toBe(401);
    });
  });
});
