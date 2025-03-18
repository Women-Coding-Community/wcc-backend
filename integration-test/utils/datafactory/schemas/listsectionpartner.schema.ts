import { linkSchema } from './link.schema';
import { partnerSchema } from './partner.schema';
export const listSectionPartnerSchema = {
  $ref: '#/definitions/listSectionPartnerSchema',
  definitions: {
    listSectionPartnerSchema: {
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
        link: { ...linkSchema.definitions.linkSchema },
        items: { ...partnerSchema.definitions.partnerSchema },
      },
      additionalProperties: false,
      required: ['title'],
    },
  },
};
