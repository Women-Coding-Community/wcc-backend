import { heroSectionSchema } from "./hero.section.schema";
import { aboutHerSchema } from "./about.her.schema";
import { commonsectionSchema } from "./commonsection.schema";

export const celebrateHerPageSchema = {
  $ref: '#/definitions/celebrateHerPageSchema',
  definitions: {
    celebrateHerPageSchema: {
      type: 'object',
      properties: {
        id: {
          type: 'string',
          minLength: 1,
          const: "page:CELEBRATE_HER"
        },
        heroSection: { ...heroSectionSchema.definitions.heroSectionSchema },
        section: { ...commonsectionSchema.definitions.commonsectionSchema },
        items: {
          type: 'array',
          items: [{ ...aboutHerSchema.definitions.aboutHerSchema }],
        },
      },
      additionalProperties: false,
      required: ['id', 'heroSection', 'section', 'items'],
    },
  },
};
