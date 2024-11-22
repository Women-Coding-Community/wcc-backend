import {expect, test} from '@playwright/test';
import { footerExpectedInformation } from '../../utils/datafactory/footer.data';
import { validateSchema } from '../../utils/helpers/schema.validation';
import { footerSchema } from '../../utils/datafactory/schemas/footer.schema';


test('GET /api/cms/v1/footer returns correct footer data', async ({request}) => {
  const response = await request.get(`/api/cms/v1/footer`);

  // response status validation
  expect(response.status()).toBe(200);

  const body = await response.json();

  // schema validation
  try {
    validateSchema(footerSchema, body);
  } catch (e: unknown) {
    if (e instanceof Error) {
      console.error(e.message); 
    } else {
      console.error('An unknown error occurred');
    }
  }

  // response body validation
  const expectedResponse = footerExpectedInformation;
  expect(body).toEqual(expectedResponse);
});
