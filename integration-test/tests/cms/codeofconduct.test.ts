import {expect, test} from '@playwright/test';
import { codeofconductExepctedInformation } from '../../utils/datafactory/codeofconduct.data';
test('GET /api/cms/v1/code-of-conduct returns correct team data', async ({request}) => {
    const response = await request.get(`https://wcc-backend.fly.dev/api/cms/v1/code-of-conduct`);
 
      if (response.status() === 200) {
      console.log('API Test Passed: Status 200');
    } else {
      console.error('API Test Failed');
    }
   
    const body = await response.json();
  
    const expectedResponse = codeofconductExepctedInformation;
  
    expect(body).toEqual(expectedResponse);
  });
