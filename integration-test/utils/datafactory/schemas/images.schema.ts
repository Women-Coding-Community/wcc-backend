export const imagesSchema = {
  $ref: '#/definitions/imagesSchema',
  definitions: {
    imagesSchema: {
      images: {
        type: 'object',
        items: {
          type: 'array',
          properties: {
            type: {
              type: 'string',
              enum: ['MOBILE', 'DESKTOP', 'TABLET'],
            },
          },

          alt: {
            type: 'string',
            minLength: 1,
          },
          path: {
            type: 'string',
            minLength: 1,
          },

          required: ['path', 'alt', 'type'],
          additionalProperties: false,
        },
      },
    },
  },
};
