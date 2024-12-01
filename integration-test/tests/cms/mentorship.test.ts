import {expect, test} from '@playwright/test';
test('GET /api/cms/v1/mentorship/overview returns correct data', async ({request}) => {
    const response = await request.get('/api/cms/v1/mentorship/overview');
 
    expect(response.status()).toBe(200);
    expect(response).toBeDefined();
   const data = await response.json();
  expect(data.page).toBeDefined();
  expect(data.mentorSection).toBeDefined();
  expect(data.menteeSection).toBeDefined();
  expect(data.feedbackSection).toBeDefined();
   
});
  
    
  
    
