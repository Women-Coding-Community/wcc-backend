import { sectionSchema } from './section.schema';
import { contactSchema } from './contact.schema';
import { memberSchema } from './member.schema';
import { paginationSchema } from './pagination.schema';
import { heroSectionSchema } from './hero.section.schema';

export const collaboratorsSchema = {
  type: 'object',
  properties: {
    id: {
      type: 'string',
      minLength: 1,
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
    page: { ...sectionSchema.definitions.sectionSchema },
    contact: { ...contactSchema.definitions.contactSchema },
    collaborators: {
      type: 'array',
      items: { ...memberSchema.definitions.memberSchema },
    },
  },
  additionalProperties: false,
  required: ['id', 'metadata', 'heroSection', 'section', 'contact', 'collaborators'],
};