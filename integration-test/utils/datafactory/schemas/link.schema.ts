export const linkSchema = {
  $ref: '#/definitions/linkSchema',
  definitions: {
    linkSchema: {
      type: 'object',
      properties: {
        title: {
          type: 'string',
          minLength: 1,
        },
        label: {
          type: 'string',
          minLength: 1,
        },
        uri: {
          type: 'string',
          format: 'uri',
          pattern: '^https?://',
          minLength: 1,
        },
      },
      required: ['title', 'label', 'uri'],
      additionalProperties: false,
    },
  },
};
