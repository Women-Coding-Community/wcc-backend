import { pageSchema } from './page.schema';
import { pageSectionSchema } from './pagesection.schema';
import { feedbackSectionSchema } from './feedback.schema';
export const mentorshipSchema = {
  $ref: '#/definitions/mentorshipSchema',
  definitions: {
    mentorshipSchema: {
      type: 'object',
      properties: {
        page: {
          link: { ...pageSchema.definitions.pageSchema },
        },
        mentorSection: {
          pageSection: {
            link: { ...pageSectionSchema.definitions.pageSectionSchema },
          },
          menteeSection: {
            pageSection: {
              link: { ...pageSectionSchema.definitions.pageSectionSchema },
            },
            feedbackSection: {
              feedbackSection: {
                link: { ...feedbackSectionSchema.definitions.feedbackSectionSchema },
              },
              additionalProperties: false,
              required: ['page', 'mentorSection', 'menteeSection', 'feedbackSection'],
            },
          },
        },
      },
    },
  },
};
