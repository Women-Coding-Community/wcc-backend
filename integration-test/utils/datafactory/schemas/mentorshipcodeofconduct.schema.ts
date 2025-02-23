import { heroSectionSchema } from './hero.section.schema';
import { sectionSchema } from './section.schema';
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
        menteeCodeSection: {
          type: 'object',
          properties: {
            listsection: { ...listsectionSchema.definitions.listsectionSchema },
          },
        },
        mentorCodeSection: {
          type: 'object',
          properties: {
            listsection: { ...listsectionSchema.definitions.listsectionSchema },
          },
        },
        wccCodeSection: {
          type: 'object',
          properties: {
            section: { ...sectionSchema.definitions.sectionSchema },
          },
        },
      },
      additionalProperties: false,
      required: ['id', 'heroSection'],
    },
  },
};
