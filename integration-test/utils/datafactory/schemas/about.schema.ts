import { pageSchema } from './page.schema';
import { contactSchema } from './contact.schema';
import { linkSchema } from './link.schema';

export const aboutSchema = {
  type: 'object',
  properties: {
    id: {
      type: 'string',
      const: 'page:ABOUT_US',
    },
    heroSection: { ...pageSchema.definitions.pageSchema },
    items: {
      type: 'array',
      items: [
        {
          type: 'object',
          properties: {
            title: {
              type: 'string',
            },
            description: {
              type: 'string',
            },
            link: { ...linkSchema.definitions.linkSchema },
            items: {
              type: 'array',
              items: [
                {
                  type: 'string',
                },
              ],
            },
          },
          additionalProperties: false,
          required: ['title'],
        },
      ],
    },
    contact: { ...contactSchema.definitions.contactSchema },
  },
  additionalProperties: false,
  required: ['id', 'heroSection', 'items', 'contact'],
};
