import { heroSectionSchema } from './hero.section.schema';
import { contactSchema } from './contact.schema';
import { listSectionStringSchema } from './listsectionstring.schema';

export const aboutSchema = {
  type: 'object',
  properties: {
    id: {
      type: 'string',
      const: 'page:ABOUT_US',
    },
    heroSection: { ...heroSectionSchema.definitions.heroSectionSchema },
    items: {
      type: 'array',
      items: [{ ...listSectionStringSchema.definitions.listSectionStringSchema }],
    },
    contact: { ...contactSchema.definitions.contactSchema },
  },
  additionalProperties: false,
  required: ['id', 'heroSection', 'items', 'contact'],
};
