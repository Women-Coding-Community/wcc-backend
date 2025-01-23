import { linkSchema } from './link.schema';

export const pageSectionSchema = {
  $ref: '#/definitions/pageSectionSchema',
  definitions: {
    pageSectionSchema: {
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
        topics: {
          type: 'array',
          items: [
            {
              type: 'string',
              minLength: 1,
            },
          ],
        },
        additionalProperties: false,
        required: ['title'],
      },
    },
  },
};
