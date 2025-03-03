import { heroSectionSchema } from './hero.section.schema';
import { listSectionFaqItemSchema } from './listsectionfaqitem.schema';

export const mentorshipfaqSchema = {
  $ref: '#/definitions/mentorshipfaqSchema',
  definitions: {
    mentorshipfaqSchema: {
      type: 'object',
      properties: {
        id: {
          type: 'string',
          minLength: 1,
          const: 'page:MENTORSHIP_FAQ',
        },
        heroSection: { ...heroSectionSchema.definitions.heroSectionSchema },
        commonFaqSection: { ...listSectionFaqItemSchema.definitions.listSectionFaqItemSchema },
        mentorsFaqSection: { ...listSectionFaqItemSchema.definitions.listSectionFaqItemSchema },
        menteesFaqSection: { ...listSectionFaqItemSchema.definitions.listSectionFaqItemSchema },
      },
      additionalProperties: false,
      required: ['id', 'heroSection', 'commonFaqSection', 'mentorsFaqSection', 'menteesFaqSection'],
    },
  },
};
