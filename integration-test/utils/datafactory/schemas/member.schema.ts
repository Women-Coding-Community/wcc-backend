import { imagesSchema } from './images.schema';
import { socialNetworkSchema } from './social.network.schema';

export const memberSchema = {
  $ref: '#/definitions/memberSchema',
  definitions: {
    memberSchema: {
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
          minLength: 1,
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
          additionalProperties: false,
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
        memberTypes: {
          type: 'array',
          items: [
            {
              type: 'string',
              enum: [
                'Director',
                'Leader',
                'Evangelist',
                'Volunteer',
                'Mentor',
                'Mentee',
                'Member',
                'Speaker',
                'Collaborator',
              ],
            },
          ],
        },
        images: { ...imagesSchema.definitions.imagesSchema },
        network: {
          type: 'array',
          items: [{ ...socialNetworkSchema.definitions.socialNetworkSchema }],
        },
      },
      required: ['fullName', 'position', 'memberTypes', 'images'], // include email, slackDisplayName, country as required when there is a fix in API
    },
  },
};
