import { imagesSchema } from './images.schema';
import { linkSchema } from './link.schema';

export const partnerSchema = {
  $ref: '#/definitions/partnerSchema',
  definitions: {
    partnerSchema: {
      type: 'array',
      properties: {
        partner: {
          type: 'object',
          properties: {
            images: {
              type: 'array',
              items: { ...imagesSchema.definitions.imagesSchema },
            },
            name: {
              type: 'string',
              minLength: 1,
            },
            description: {
              type: 'string',
              minLength: 1,
            },
            link: { ...linkSchema.definitions.linkSchema },
          },
        },
      },

      required: ['images', 'name', 'description', 'link'],
      additionalProperties: false,
    },
  },
};
