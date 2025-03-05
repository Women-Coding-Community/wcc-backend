import { linkSchema } from './link.schema';
import { eventSchema } from './event.schema';

export const listSectionEventSchema = {
  $ref: '#/definitions/listSectionEventSchema',
  definitions: {
    listSectionEventSchema: {
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
          items: [{ ...eventSchema.definitions.eventSchema }],
        },
      },
      additionalProperties: false,
      required: ['title'],
    },
  },
};
