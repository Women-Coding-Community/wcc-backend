import { linkSchema } from './link.schema';
import { imagesSchema } from './images.schema';
import { leadershipMemberSchema } from './leadershipmember.schema';
export const teamSchema = {
  type: 'object',
  properties: {
    id: {
      type: 'string',
      const: 'page:TEAM',
    },
    page: {
      type: 'object',
      properties: {
        id: {
          type: 'string',
          minLength: 1,
        },
        title: {
          type: 'string',
          const: 'Team',
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
      },
      additionalProperties: false,
      required: ['title'],
    },
    contact: {
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
          items: [
            {
              type: 'object',
              properties: {
                type: {
                  type: 'string',
                  enum: [
                    'youtube',
                    'github',
                    'linkedin',
                    'instagram',
                    'facebook',
                    'twitter',
                    'medium',
                    'slack',
                    'meetup',
                    'email',
                    'unknown',
                    'DEFAULT_LINK ',
                  ],
                },
                link: {
                  type: 'string',
                },
              },
              required: ['type', 'link'],
              additionalProperties: false,
              if: {
                properties: {
                  type: { const: 'email' },
                },
              },
              then: {
                properties: {
                  link: {
                    pattern: '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$', // Custom email regex
                  },
                },
              },
              else: {
                properties: {
                  link: {
                    format: 'uri', // Format as URI
                    pattern: '^https?://', // URL pattern
                    minLength: 1,
                  },
                },
              },
            },
          ],
        },
      },
      required: ['title', 'links'],
    },
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
        membersByType: {
          type: 'object',
          properties: {
            directors: {
              type: 'array',
              items: { ...leadershipMemberSchema.definitions.leadershipMemberSchema },
            },
            leads: {
              type: 'array',
              items: { ...leadershipMemberSchema.definitions.leadershipMemberSchema },
            },
            evangelists: {
              type: 'array',
              items: { ...leadershipMemberSchema.definitions.leadershipMemberSchema },
            },
          },
          additionalProperties: false,
          required: ['directors', 'leads', 'evangelists'],
        },
      },
      additionalProperties: false,
      required: ['id', 'page', 'contact', 'membersByType'],
    },
  },
};
