import {expect, test} from '@playwright/test';

test('GET /api/cms/v1/collaborators returns correct data', async ({request}) => {
    const response = await request.get('/api/cms/v1/collaborators');
    expect(response.status()).toBe(200);
    expect(response).toBeDefined();
    const data = await response.json();
    expect(data.page).toBeDefined();
    expect(data.page.title).toBe('Team');
    expect(data.contact).toBeDefined();
    expect(data.contact.title).toBe('Contact us');  
});