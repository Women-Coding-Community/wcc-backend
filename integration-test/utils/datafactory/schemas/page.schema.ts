import { linkSchema } from './link.schema';
import { imagesSchema } from './images.schema';
import { customStyleSchema } from './custom.style.schema';

export const pageSchema = {
  $ref: '#/definitions/pageSchema',
  definitions: {
    pageSchema: {
      type: 'object',
      properties: {
        id: {
          type: 'string',
          minLength: 1,
        },
        title: {
          type: 'string',
          minLength: 1,
        },
        subtitle: {
          type: 'string',
          minLength: 1,
        },
        description: {
          type: 'string',
          minLength: 1,
        },
        link: { ...linkSchema.definitions.linkSchema },
        images: { ...imagesSchema.definitions.imagesSchema },
        customStyle: { ...customStyleSchema.definitions.customStyleSchema },
      },
      additionalProperties: false,
      required: ['title'],
    },
  },
};
