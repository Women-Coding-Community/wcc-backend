export const customstyleSchema = {
  $ref: '#/definitions/customstyleSchema',
  definitions: {
    customstyleSchema: {
      backgroundcolor: {
        type: 'object',
        properties: {
          items: {
            type: 'array',
            color: {
              type: 'string',
              enum: ['primary', 'secondary', 'tertiary'],
            },
            shade: {
              type: 'string',
              minLength: 1,
            },
            name: {
              type: 'string',
              enum: [' main', 'light', 'dark'],
              value: {
                type: 'integer',
              },
              additionalProperties: false,
              required: ['color', 'shade', 'name', 'value'],
            },
          },
        },
      },
    },
  },
};
