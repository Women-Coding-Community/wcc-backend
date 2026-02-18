export const socialNetworkSchema = {
  $ref: '#/definitions/socialNetworkSchema',
  definitions: {
    socialNetworkSchema: {
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
            'website',
            'medium',
            'slack',
            'meetup',
            'email',
            'unknown',
            'default_link',
          ],
        },
        link: {
          type: 'string',
          minLength: 1,
        },
      },
      additionalProperties: false,
      required: ['type', 'link'],
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
  },
};
