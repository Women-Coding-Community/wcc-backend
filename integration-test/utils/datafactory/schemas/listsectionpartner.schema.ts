import { linkSchema } from './link.schema';
import { itemsSchema } from './items.schema';
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
        items: { ...itemsSchema.definitions.itemsSchema },
      },
      additionalProperties: false,
      required: ['title'],
    },
  },
};
