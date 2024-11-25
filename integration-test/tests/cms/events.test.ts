import {expect, test} from '@playwright/test';
import { eventsExepctedInformation } from '../../utils/datafactory/events.data';
test('GET /api/cms/v1/code-of-conduct returns correct team data', async ({request}) => {
    const response = await request.get(`https://wcc-backend.fly.dev/api/cms/v1/events`);
 
      if (response.status() === 200) {
      console.log('API Test Passed: Status 200');
    } else {
      console.error('API Test Failed');
    }
   
    const body = await response.json();
  
    const expectedResponse = eventsExepctedInformation;
  
    expect(body).toEqual(expectedResponse);
  });

