import {expect, test} from '@playwright/test';
import { footerExpectedInformation } from '../../utils/datafactory/footer.data';

test('GET /api/cms/v1/footer returns correct footer data', async ({request}) => {
  const response = await request.get(`/api/cms/v1/footer`);

  expect(response.status()).toBe(200);

  const body = await response.json();

  const expectedResponse = footerExpectedInformation;

  expect(body).toEqual(expectedResponse);
});
