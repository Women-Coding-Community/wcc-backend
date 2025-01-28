import { pageSchema } from './page.schema';
import { linkSchema } from './link.schema';

export const codeofconductSchema = {
  $ref: '#/definitions/pageSchema',
  definitions: {
    codeofconductSchema: {
      type: 'object',
      properties: {
        id: {
          type: 'string',
          minLength: 1,
        },
        page: {
          page: { ...pageSchema.definitions.pageSchema },
        },
        items: {
          SectionString: {
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
                type: 'string',
                minLength: 1,
              },
              additionalProperties: false,
              required: ['id', 'page', 'items'],
            },
          },
        },
      },
    },
  },
};
