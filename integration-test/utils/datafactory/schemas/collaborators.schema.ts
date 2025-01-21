import { pageSchema } from './page.schema';
import { contactSchema } from './contact.schema';
import { leadershipMemberSchema } from './leadershipmember.schema';
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
      items: { ...leadershipMemberSchema.definitions.leadershipMemberSchema },
    },
  },
  additionalProperties: false,
  required: ['metadata', 'page', 'contact', 'collaborators'],
};
