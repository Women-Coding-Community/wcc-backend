import { linkSchema } from './link.schema';
export const listSectionFaqItemSchema = {
  $ref: '#/definitions/listSectionFaqItemSchema',
  definitions: {
    listSectionFaqItemSchema: {
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
              //  "type": "object",
              //   "properties": {
              question: {
                type: 'string',
              },
              answer: {
                type: 'string',
              },
            },
          ],
          additionalProperties: false,
          required: ['question', 'answer'],
        },
      },

      additionalProperties: false,
      required: ['title'],
    },
  },
};
