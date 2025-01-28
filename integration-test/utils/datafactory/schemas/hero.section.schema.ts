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
        description: {
          type: 'string',
          minLength: 1,
        },
        image: { ...imagesSchema.definitions.imagesSchema },
        customStyle: { ...customStyleSchema.definitions.customStyleSchema },
      },
      additionalProperties: false,
      required: ['title', 'image'],
    },
  },
};
