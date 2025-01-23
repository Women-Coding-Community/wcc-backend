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
        },
        page: {
          page: { ...pageSchema.definitions.pageSchema },
        },
        mentorSection: {
          pageSection: {
            pageSection: { ...pageSectionSchema.definitions.pageSectionSchema },
          },
          menteeSection: {
            pageSection: {
              pageSection: { ...pageSectionSchema.definitions.pageSectionSchema },
            },
            feedbackSection: {
              feedbackSection: {
                link: { ...feedbackSectionSchema.definitions.feedbackSectionSchema },
              },
              additionalProperties: false,
              required: ['id', 'page', 'mentorSection', 'menteeSection'],
            },
          },
        },
      },
    },
  },
};
