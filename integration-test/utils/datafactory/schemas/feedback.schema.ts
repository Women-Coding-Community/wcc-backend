export const feedbackSectionSchema = {
  $ref: '#/definitions/feedbackSectionSchema',
  definitions: {
    feedbackSectionSchema: {
      type: 'object',
      properties: {
        title: {
          type: 'string',
          minLength: 1,
        },
        feedbacks: {
          type: 'array',
          items: [
            {
              type: 'object',
              properties: {
                name: {
                  type: 'string',
                  minLength: 1,
                },
                feedback: {
                  type: 'string',
                  minLength: 1,
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
                  additionalProperties: false,
                  required: ['name', 'feedback', 'year'],
                },
              },
            },
          ],
        },
      },
      additionalProperties: false,
      required: ['title', 'feedbacks'],
    },
  },
};
