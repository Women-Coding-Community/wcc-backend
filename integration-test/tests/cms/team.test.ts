import {expect, test} from '@playwright/test';
test('GET /api/cms/v1/team returns correct data', async ({request}) => {
    const response = await request.get(`/api/cms/v1/team`);
 
  expect(response.status()).toBe(200);
  expect(response).toBeDefined();
  const data = await response.json();
  expect(data.page).toBeDefined();
  expect(data.contact).toBeDefined();
  expect(data.membersByType).toBeDefined();
  
  
   
});
  