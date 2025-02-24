import { heroSectionSchema } from "./hero.section.schema";
import { aboutHerSchema } from "./about.her.schema";

export const celebrateHerOverviewSchema = {
  $ref: '#/definitions/celebrateHerOverviewSchema',
  definitions: {
    celebrateHerOverviewSchema: {
      type: 'object',
      properties: {
        id: {
          type: 'string',
          minLength: 1,
          const: ""
        },
        heroSection: { ...heroSectionSchema.definitions.heroSectionSchema },
        items: {
          type: 'array',
          items: [{ ...aboutHerSchema.definitions.aboutHerSchema }],
        },
      },
      additionalProperties: false,
      required: ['id', 'heroSection', 'items'],
    },
  },
};
