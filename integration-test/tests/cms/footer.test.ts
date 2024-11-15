import {expect, request, test} from '@playwright/test';
import ApiHost from "../ApiHost";

test('GET /api/cms/v1/footer returns correct footer data', async () => {
  const apiHost = ApiHost.get();
  const context = await request.newContext();

  const response = await context.get(`${apiHost}/api/cms/v1/footer`);

  expect(response.status()).toBe(200);

  const body = await response.json();
  expect(body).toHaveProperty('title', 'Follow Us');
  expect(body).toHaveProperty('subtitle', 'Join us on social media and stay tuned.');
  expect(body).toHaveProperty('description', 'Women Coding Community is a not-for-profit organisation. © 2024 Women Coding Community');
  expect(body.network).toBeInstanceOf(Array);
  expect(body.network.length).toBe(6);
  expect(body.network[0]).toHaveProperty('type', 'linkedIn');
  expect(body.network[0]).toHaveProperty('link', 'https://www.linkedin.com/company/womencodingcommunity');
  expect(body.network[1]).toHaveProperty('type', 'twitter');
  expect(body.network[1]).toHaveProperty('link', 'https://twitter.com/WCC_Community');
  expect(body.network[2]).toHaveProperty('type', 'github');
  expect(body.network[2]).toHaveProperty('link', 'https://github.com/WomenCodingCommunity');
  expect(body.network[3]).toHaveProperty('type', 'instagram');
  expect(body.network[3]).toHaveProperty('link', 'https://www.instagram.com/women_coding_community');
  expect(body.network[4]).toHaveProperty('type', 'email');
  expect(body.network[4]).toHaveProperty('link', 'london@womencodingcommunity.com');
  expect(body.network[5]).toHaveProperty('type', 'slack');
  expect(body.network[5]).toHaveProperty('link', 'https://join.slack.com/t/womencodingcommunity/shared_invite/zt-2hpjwpx7l-rgceYBIWp6pCiwc0hVsX8A');
  expect(body).toHaveProperty('link');
  expect(body.link).toHaveProperty('title', 'Experiencing Technical Issues?');
  expect(body.link).toHaveProperty('label', 'Send us a report');
  expect(body.link).toHaveProperty('uri', 'https://github.com/WomenCodingCommunity/WomenCodingCommunity.github.io/issues/new?template=bug_report.md&title=bug%20title');
});