import {expect, test} from '@playwright/test';
import { footerExpectedInformation } from '../../utils/datafactory/footer.data';
import { validateSchema } from '../../utils/helpers/schema.validation';
import { footerSchema } from '../../utils/datafactory/schemas/footer.schema';

const data = {
  "title": "Follow Us",
  "subtitle": "Join us on social media and stay tuned.",
  "description": "Women Coding Community is a not-for-profit organisation. © 2024 Women Coding Community",
  "network": [{
      "type": "linkedIn",
      "link": "https://www.linkedin.com/company/womencodingcommunity"
    },
    {
      "type": "twitter",
      "link": "http://twitter.com/WCC_Community"
    },
    {
      "type": "github",
      "link": "https://github.com/WomenCodingCommunity"
    },
    {
      "type": "instagram",
      "link": "https://www.instagram.com/women_coding_community"
    },
    {
      "type": "email",
      "link": "london@womencodingcommunity.com"
    },
    {
      "type": "slack",
      "link": "https://join.slack.com/t/womencodingcommunity/shared_invite/zt-2hpjwpx7l-rgceYBIWp6pCiwc0hVsX8A"
    }
  ],
  "link": {
    "title": "Experiencing Technical Issues?",
    "label": "Send us a report",
    "uri": "https://github.com/WomenCodingCommunity/WomenCodingCommunity.github.io/issues/new?template=bug_report.md&title=bug%20title"
  }
}


test('GET /api/cms/v1/footer returns correct footer data', async ({request}) => {
  const response = await request.get(`/api/cms/v1/footer`);

  expect(response.status()).toBe(200);

  const body = await response.json();

  // schema validation
  validateSchema(footerSchema, data)

  const expectedResponse = footerExpectedInformation;

  expect(body).toEqual(expectedResponse);
});
