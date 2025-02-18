import { paginationSchema } from './pagination.schema';
import { contactSchema } from './contact.schema';
import { heroSectionSchema } from './hero.section.schema';
import { eventSchema } from './event.schema';
import { sectionSchema } from './section.schema';

export const eventsSchema = {
  type: 'object',
  properties: {
    metadata: {
      type: 'object',
      properties: {
        pagination: { ...paginationSchema.definitions.paginationSchema },
      },
      additionalProperties: false,
      required: ['pagination'],
    },
    heroSection: { ...heroSectionSchema.definitions.heroSectionSchema },
    page: { ...sectionSchema.definitions.sectionSchema },
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
};
