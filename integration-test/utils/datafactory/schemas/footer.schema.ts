import { linkSchema } from './link.schema';
import { socialNetworkSchema } from './social.network.schema';

export const footerSchema = {
  $ref: '#/definitions/footerSchema',
  definitions: {
    footerSchema: {
      type: 'object',
      properties: {
        id: {
          type: 'string',
          const: 'page:FOOTER',
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
        network: {
          type: 'array',
          items: [{ ...socialNetworkSchema.definitions.socialNetworkSchema }],
        },
        link: { ...linkSchema.definitions.linkSchema },
      },
      required: ['id', 'title', 'subtitle', 'description', 'network', 'link'],
      additionalProperties: false,
    },
  },
};
