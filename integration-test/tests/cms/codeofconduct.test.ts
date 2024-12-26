import { expect, test } from '@playwright/test';
import { codeofconductExepctedInformation } from '@utils/datafactory/codeofconduct.data';

test('GET /api/cms/v1/code-of-conduct returns correct data', async ({ request }) => {
  const response = await request.get(`/api/cms/v1/code-of-conduct`);

  expect(response.status()).toBe(200);
  const body = await response.json();

  const expectedResponse = codeofconductExepctedInformation;

  expect(body).toEqual(expectedResponse);
});

