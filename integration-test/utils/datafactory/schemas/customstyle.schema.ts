export const customstyleSchema = {
  $ref: '#/definitions/customstyleSchema',
  definitions: {
    customstyleSchema: {
      type: 'object',
      properties: {
        backgroundColour: {
          type: 'object',
          properties: {
            color: {
              type: 'string',
              enum: ['primary', 'secondary', 'tertiary'],
            },
            shade: {
              type: 'object',
              properties: {
                name: {
                  type: 'string',
                  enum: ['main', 'light', 'dark'],
                },
                value: {
                  type: 'integer',
                },
              },
              additionalProperties: false,
            },
          },
          additionalProperties: false,
        },
      },
      additionalProperties: false,
    },
  },
};
