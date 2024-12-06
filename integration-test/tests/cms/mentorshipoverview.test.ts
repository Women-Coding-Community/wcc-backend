import {expect, test} from '@playwright/test';
test('GET /api/cms/v1/mentorship/overview returns correct data', async ({request}) => {
const response = await request.get('/api/cms/v1/mentorship/overview');
expect(response.status()).toBe(200);
const data = await response.json();
expect(data.page).toBeDefined();
expect(data.page.title).toBe('Mentorship Programme'); 
expect(data.mentorSection).toBeDefined();
expect(data.mentorSection.title).toBe('Become a Mentor');
expect(data.menteeSection).toBeDefined();
expect(data.menteeSection.title).toBe('Become a Mentee');
});
