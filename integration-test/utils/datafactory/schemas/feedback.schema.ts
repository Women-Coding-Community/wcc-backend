export const feedbackSectionSchema = {
  $ref: '#/definitions/feebackSchema',
  definitions: {
    feedbackSectionSchema: {
      type: 'object',
      properties: {
        title: {
          type: 'string',
        },
        feedbacks: {
          type: 'array',
          items: [
            {
              type: 'object',
              properties: {
                name: {
                  type: 'string',
                },
                feedback: {
                  type: 'string',
                },
                mentee: {
                  type: 'boolean',
                },
                year: {
                  type: 'object',
                  properties: {
                    value: {
                      type: 'integer',
                    },
                    leap: {
                      type: 'boolean',
                    },
                  },
                  required: ['value', 'leap'],
                },
              },
              additionalProperties: false,
              required: ['name', 'feedback', 'mentee', 'year'],
            },
          ],
        },
      },
      additionalProperties: false,
      required: ['title', 'feedbacks'],
    },
  },
};
