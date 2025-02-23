import { heroSectionSchema } from './hero.section.schema';
import { commonsectionSchema } from './commonsection.schema';
import { listsectionSchema } from './listsection.schema';

export const mentorshipcodeofconductSchema = {
  $ref: '#/definitions/mentorshipcodeofconductSchema',
  definitions: {
    mentorshipcodeofconductSchema: {
      type: 'object',
      properties: {
        id: {
          type: 'string',
          minLength: 1,
          const: 'page:MENTORSHIP_CODE_OF_CONDUCT',
        },
        heroSection: { ...heroSectionSchema.definitions.heroSectionSchema },
        menteeCodeSection: { ...listsectionSchema.definitions.listsectionSchema },
        mentorCodeSection: { ...listsectionSchema.definitions.listsectionSchema },
        wccCodeSection: { ...commonsectionSchema.definitions.commonsectionSchema },
      },
      additionalProperties: false,
      required: ['id', 'heroSection'],
    },
  },
};
