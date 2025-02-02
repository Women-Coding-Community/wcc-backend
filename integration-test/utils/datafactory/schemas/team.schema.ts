import { leadershipMemberSchema } from './leadershipmember.schema';
import { heroSectionSchema } from './hero.section.schema';
import { pageSchema } from './page.schema';
import { contactSchema } from './contact.schema';

export const teamSchema = {
  type: 'object',
  properties: {
    id: {
      type: 'string',
      const: 'page:TEAM',
    },
    heroSection: { ...heroSectionSchema.definitions.heroSectionSchema },
    page: { ...pageSchema.definitions.pageSchema },
    contact: { ...contactSchema.definitions.contactSchema },
    membersByType: {
      type: 'object',
      properties: {
        directors: {
          type: 'array',
          items: { ...leadershipMemberSchema.definitions.leadershipMemberSchema },
        },
        leads: {
          type: 'array',
          items: { ...leadershipMemberSchema.definitions.leadershipMemberSchema },
        },
        evangelists: {
          type: 'array',
          items: { ...leadershipMemberSchema.definitions.leadershipMemberSchema },
        },
      },
      additionalProperties: false,
      required: ['directors', 'leads', 'evangelists'],
    },
  },
  additionalProperties: false,
  required: ['id', 'heroSection', 'page', 'contact', 'membersByType'],
};
