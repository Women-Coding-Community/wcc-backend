import { imagesSchema } from './images.schema';
import { customStyleSchema } from './custom.style.schema';

export const heroSectionSchema = {
  $ref: '#/definitions/heroSectionSchema',
  definitions: {
    heroSectionSchema: {
      type: 'object',
      properties: {
        title: {
          type: 'string',
          minLength: 1,
        },
        subtitle: {
          type: 'string',
          minLength: 1,
        },
        images: {
          type: 'array',
          items: { ...imagesSchema.definitions.imagesSchema },
        },
        customStyle: { ...customStyleSchema.definitions.customStyleSchema },
      },
      additionalProperties: false,
      required: ['title'],
    },
  },
};
