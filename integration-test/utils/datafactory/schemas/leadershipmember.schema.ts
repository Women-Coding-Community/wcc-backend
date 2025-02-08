import { imagesSchema } from './images.schema';
import { socialNetworkSchema } from './social.network.schema';

export const leadershipMemberSchema = {
  $ref: '#/definitions/leadershipMemberSchema',
  definitions: {
    leadershipMemberSchema: {
      type: 'object',
      properties: {
        fullName: {
          type: 'string',
          minLength: 1,
        },
        position: {
          type: 'string',
          minLength: 1,
        },
        email: {
          type: 'string',
          pattern: '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$',
        },
        slackDisplayName: {
          type: 'string',
          minLength: 1,
        },
        country: {
          type: 'object',
          properties: {
            countryCode: {
              type: 'string',
              minLength: 1,
            },
            countryName: {
              type: 'string',
              minLength: 1,
            },
          },
          required: ['countryCode', 'countryName'],
        },
        city: {
          type: 'string',
          minLength: 1,
        },
        companyName: {
          type: 'string',
          minLength: 1,
        },

        images: { ...imagesSchema.definitions.imagesSchema },
        network: {
          type: 'array',
          items: [{ ...socialNetworkSchema.definitions.socialNetworkSchema }],
        },
      },
      additionalProperties: false,
      required: ['fullName', 'position', 'images'], // TODO include email, slackDisplayName, country as required when there is a fix in API
    },
  },
};
