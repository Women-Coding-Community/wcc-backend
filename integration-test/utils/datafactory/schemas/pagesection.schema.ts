import { linkSchema } from './link.schema';

export const pageSectionSchema = {
  $ref: '#/definitions/pageSectionSchema',
  definitions: {
    pageSectionSchema: {
      type: 'object',
      properties: {
        title: {
          type: 'string',
        },
        description: {
          type: 'string',
        },
        link: { ...linkSchema.definitions.linkSchema },
      },
      topics: {
        type: 'array',
        items: [
          {
            type: 'string',
          },
        ],
      },

      additionalProperties: false,
      required: ['title'],
      minItems: 1,
      maxItems: 1,
    },
  },
};
