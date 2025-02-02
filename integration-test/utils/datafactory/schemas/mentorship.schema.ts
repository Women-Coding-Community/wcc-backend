import { pageSchema } from './page.schema';
import { pageSectionSchema } from './pagesection.schema';
import { feedbackSectionSchema } from './feedback.schema';
import { heroSectionSchema } from './hero.section.schema';

export const mentorshipSchema = {
  $ref: '#/definitions/mentorshipSchema',
  definitions: {
    mentorshipSchema: {
      type: 'object',
      properties: {
        id: {
          type: 'string',
          minLength: 1,
          const: 'page:MENTORSHIP_OVERVIEW',
        },
        heroSection: { ...heroSectionSchema.definitions.heroSectionSchema },
        page: { ...pageSchema.definitions.pageSchema },
        mentorSection: { ...pageSectionSchema.definitions.pageSectionSchema },
        menteeSection: { ...pageSectionSchema.definitions.pageSectionSchema },
        feedbackSection: { ...feedbackSectionSchema.definitions.feedbackSectionSchema },
      },

      additionalProperties: false,
      required: ['id', 'heroSection', 'page', 'mentorSection', 'menteeSection'],
    },
  },
};
