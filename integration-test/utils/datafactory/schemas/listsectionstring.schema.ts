import { linkSchema } from './link.schema';
export const listSectionStringSchema = {
  $ref: '#/definitions/listSectionStringSchema',
  definitions: {
    listSectionStringSchema: {
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
          items: [
            {
              type: 'string',
            },
          ],
        },
      },
    },
    additionalProperties: false,
    required: ['title'],
  },
};
