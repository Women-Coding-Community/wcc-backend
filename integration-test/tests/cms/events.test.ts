import {expect, test} from '@playwright/test';
test('GET /api/cms/v1/events returns correct data', async ({request}) => {
    const response = await request.get(`/api/cms/v1/events`);
    // response status validation
    expect(response.status()).toBe(200);
    const body = await response.json();
  });
  
   