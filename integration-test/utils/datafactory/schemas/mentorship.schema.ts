import { linkSchema } from './link.schema';
import { imagesSchema } from './images.schema';
import { customstyleSchema } from './customstyle.schema';
export const mentorshipSchema = {
  $ref: '#/definitions/mentorshipSchema',
  definitions: {
    mentorshipSchema: {
      page: {
        type: 'object',
        const: 'page:MENTORSHIP',
        properties: {
          id: {
            type: 'string',
            minLength: 1,
          },
        },
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
        customStyle: { ...customstyleSchema.definitions.customstyleSchema },
        additionalProperties: false,
        required: ['id', 'title', 'subtitle', 'description', 'link', 'images', 'customStyle'],
      },
    },
  },
  mentorSection: {
    type: 'object',
    properties: {
      title: {
        type: 'string',
      },
      description: {
        type: 'string',
      },
      link: { ...linkSchema.definitions.linkSchema },
      topics: {
        type: 'string',
      },
      additionalProperties: false,
      required: ['title', 'description', 'link', 'topics'],
    },
    menteeSection: {
      type: 'object',
      properties: {
        title: {
          type: 'string',
        },
        description: {
          type: 'string',
        },
        link: {
          type: 'object',
          properties: {
            title: {
              type: 'string',
            },
            label: {
              type: 'string',
            },
            uri: {
              type: 'string',
            },
          },
          required: ['title', 'label', 'uri'],
        },
        topics: {
          type: 'array',
          items: [
            {
              type: 'string',
            },
          ],
        },

        additionalProperties: false,
        required: ['title', 'description', 'link', 'topics'],
      },
      feedbackSection: {
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
                required: ['name', 'feedback', 'mentee', 'year'],
              },
            ],
          },
        },
        additionalProperties: false,
        required: ['title', 'feedbacks'],
      },
    },
    additionalProperties: false,
    required: ['page', 'mentorSection', 'menteeSection', 'feedbackSection'],
  },
};
