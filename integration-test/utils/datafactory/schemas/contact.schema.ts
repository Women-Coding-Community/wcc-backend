import { socialNetworkSchema } from './social.network.schema';

export const contactSchema = {
  $ref: '#/definitions/contactSchema',
  definitions: {
    contactSchema: {
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
        links: {
          type: 'array',
          items: [{ ...socialNetworkSchema.definitions.socialNetworkSchema }],
        },
      },
      additionalProperties: false,
      required: ['title', 'links'],
    },
  },
};
