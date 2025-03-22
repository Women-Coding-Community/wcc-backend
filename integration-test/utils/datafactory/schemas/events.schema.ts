import { paginationSchema } from './pagination.schema';
import { contactSchema } from './contact.schema';
import { heroSectionSchema } from './hero.section.schema';
import { eventSchema } from './event.schema';
import { commonsectionSchema } from './commonsection.schema';

export const eventsSchema = {
  type: 'object',
  properties: {
    id: {
      type: 'string',
      const: 'page:EVENTS',
    },
    metadata: {
      type: 'object',
      properties: {
        pagination: { ...paginationSchema.definitions.paginationSchema },
      },
      additionalProperties: false,
      required: ['pagination'],
    },
    heroSection: { ...heroSectionSchema.definitions.heroSectionSchema },
    section: { ...commonsectionSchema.definitions.commonsectionSchema },
    contact: { ...contactSchema.definitions.contactSchema },
    data: {
      type: 'object',
      properties: {
        items: {
          type: 'array',
          items: [{ ...eventSchema.definitions.eventSchema }],
        },
      },
      additionalProperties: false,
    },
  },
  additionalProperties: false,
  required: ['id', 'metadata', 'heroSection', 'section', 'contact', 'data']
};
