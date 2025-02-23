import { heroSectionSchema } from './hero.section.schema';
import { pageSchema } from './page.schema';
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
            page: { ...pageSchema.definitions.pageSchema },
          },
        },
      },
      additionalProperties: false,
      required: ['id', 'heroSection'],
    },
  },
};
