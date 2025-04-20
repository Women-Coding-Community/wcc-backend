import { expect, test } from '@playwright/test';
import { eventsfiltersSchema } from '@utils/datafactory/schemas/eventsfilters.schema';
import { validateSchema } from '@utils/helpers/schema.validation';
import { eventFiltersData } from '@utils/datafactory/test-data/event.filters.data';
import { PATHS } from '@utils/datafactory/paths.data';
import { createOrUpdatePage } from '@utils/helpers/preconditions';

test.describe('Validate positive test cases for EVENTS FILTERS API', () => {
  test.beforeEach(async ({ request }) => {
    const url = `${PATHS.PLATFORM_PAGE}?pageType=EVENT_FILTERS`;
    await createOrUpdatePage(request, 'EVENTS FILTERS Page', url, eventFiltersData);
  });

  test('GET /api/cms/v1/events/filters returns correct data', async ({ request }) => {
    const response = await request.get(PATHS.EVENTS_FILTERS);

    // response status validation
    expect(response.status()).toBe(200);

    const body = await response.json();

    // schema validation
    try {
      validateSchema(eventsfiltersSchema, body);
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
      const response = await request.get(PATHS.EVENTS_FILTERS, {
        headers: headers,
      });
      expect(response.status()).toBe(401);
    });
  });
});
