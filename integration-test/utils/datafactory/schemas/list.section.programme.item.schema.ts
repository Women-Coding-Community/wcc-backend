import { linkSchema } from './link.schema';
import { programmeItemSchema } from './programme.item.schema';

export const listSectionProgrammeItemSchema = {
  $ref: '#/definitions/listSectionProgrammeItemSchema',
  definitions: {
    listSectionProgrammeItemSchema: {
      type: 'object',
      properties: {
        title: {
          type: 'string',
          minLength: 1,
        },
        description: {
          type: 'string',
          minLength: 1,
        },
        link: { ...linkSchema.definitions.linkSchema },
        items: {
          type: 'array',
          items: [{ ...programmeItemSchema.definitions.programmeItemSchema }],
        },
      },
      additionalProperties: false,
      required: ['title'],
    },
  },
};
