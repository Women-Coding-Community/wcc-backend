import { commonsectionSchema } from './commonsection.schema';
import { listSectionStringSchema } from './listsectionstring.schema';
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
        section: { ...commonsectionSchema.definitions.commonsectionSchema },
        mentorSection: { ...listSectionStringSchema.definitions.listSectionStringSchema },
        menteeSection: { ...listSectionStringSchema.definitions.listSectionStringSchema},
        feedbackSection: { ...feedbackSectionSchema.definitions.feedbackSectionSchema },
      },

      additionalProperties: false,
      required: ['id', 'heroSection', 'section', 'mentorSection', 'menteeSection'],
    },
  },
};