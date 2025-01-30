import { pageSchema } from './page.schema';
import { pageSectionSchema } from './pagesection.schema';
import { feedbackSectionSchema } from './feedback.schema';

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
        page: { ...pageSchema.definitions.pageSchema },

        mentorSection: {
          pageSection: { ...pageSectionSchema.definitions.pageSectionSchema },
        },
        menteeSection: {
          pageSection: { ...pageSectionSchema.definitions.pageSectionSchema },
        },
        feedbackSection: {
          feedbackSection: { ...feedbackSectionSchema.definitions.feedbackSectionSchema },
        },
      },

      additionalProperties: false,
      required: ['id', 'page', 'mentorSection', 'menteeSection'],
    },
  },
};
