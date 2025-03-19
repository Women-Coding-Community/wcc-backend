import { listSectionStringSchema } from './listsectionstring.schema';
import { heroSectionSchema } from './hero.section.schema';
import { contactSchema } from './contact.schema';
import { listSectionPartnerSchema } from './listsectionpartner.schema';

export const partnersSchema = {
  $ref: '#/definitions/partnersSchema',
  definitions: {
    partnersSchema: {
      type: 'object',
      properties: {
        id: {
          type: 'string',
          minLength: 1,
          const: 'page:PARTNERS',
        },
        heroSection: { ...heroSectionSchema.definitions.heroSectionSchema },
        introSection: { ...listSectionStringSchema.definitions.listSectionStringSchema },
        contact: { ...contactSchema.definitions.contactSchema },
        partners: { ...listSectionPartnerSchema.definitions.listSectionPartnerSchema },
      },
      additionalProperties: false,
      required: ['id', 'heroSection', 'introSection', 'contact'],
    },
  },
};
