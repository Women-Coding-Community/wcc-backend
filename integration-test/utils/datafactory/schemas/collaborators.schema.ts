import { pageSchema } from './page.schema';
import { contactSchema } from './contact.schema';
import { memberSchema } from './member.schema';
import { paginationSchema } from './pagination.schema';

export const collaboratorsSchema = {
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
    page: { ...pageSchema.definitions.pageSchema },
    contact: { ...contactSchema.definitions.contactSchema },
    collaborators: {
      type: 'array',
      items: { ...memberSchema.definitions.memberSchema },
    },
  },
  additionalProperties: false,
  required: ['metadata', 'page', 'contact', 'collaborators'],
};
