import { commonsectionSchema } from './commonsection.schema';
import { linkSchema } from './link.schema';
import { heroSectionSchema } from './hero.section.schema';

export const codeofconductSchema = {
  $ref: '#/definitions/codeofconductSchema',
  definitions: {
    codeofconductSchema: {
      type: 'object',
      properties: {
        id: {
          type: 'string',
          minLength: 1,
        },
        heroSection: { ...heroSectionSchema.definitions.heroSectionSchema },
        section: { ...commonsectionSchema.definitions.commonsectionSchema },
        items: {
          type: 'array',
          items: [
            {
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
                items: {
                  type: 'array',
                  items: [
                    {
                      type: 'string',
                      minLength: 1,
                    },
                  ],
                },
              },
              required: ['title'],
            },
          ],
        },
      },
      additionalProperties: false,
      required: ['id', 'heroSection', 'section', 'items'],
    },
  },
};
