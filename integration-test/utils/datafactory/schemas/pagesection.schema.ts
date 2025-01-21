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
        link: {
          type: 'object',
          properties: {
            title: {
              type: 'string',
            },
            label: {
              type: 'string',
            },
            uri: {
              type: 'string',
            },
          },
          required: ['title', 'label', 'uri'],
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
  },
};
