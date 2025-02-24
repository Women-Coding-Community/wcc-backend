import { heroSectionSchema } from './hero.section.schema';
import { commonsectionSchema } from './commonsection.schema';
import { listSectionStringSchema } from './listsectionstring.schema';

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
        menteeCodeSection: { ...listSectionStringSchema.definitions.listSectionStringSchema },
        mentorCodeSection: { ...listSectionStringSchema.definitions.listSectionStringSchema },
        wccCodeSection: { ...commonsectionSchema.definitions.commonsectionSchema },
      },
      additionalProperties: false,
      required: ['id', 'heroSection'],
    },
  },
};
