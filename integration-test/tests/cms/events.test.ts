import {expect, test} from '@playwright/test';
import { eventsExepctedInformation } from '../../utils/datafactory/events.data';
test('GET /api/cms/v1/events returns correct data', async ({request}) => {
    const response = await request.get(`/api/cms/v1/events`);
 
    expect(response.status()).toBe(200);
   
    const body = await response.json();
  
    const expectedResponse = eventsExepctedInformation;
  
    expect(body).toEqual(expectedResponse);
  });

