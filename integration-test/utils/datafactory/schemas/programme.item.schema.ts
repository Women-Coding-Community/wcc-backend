import { linkSchema } from './link.schema';

export const programmeItemSchema = {
  $ref: '#/definitions/programmeItemSchema',
  definitions: {
    programmeItemSchema: {
      type: 'object',
      properties: {
        name: {
          type: 'string',
          enum: [
            'Book Club',
            'Coding Club Python',
            'Career Club',
            'Speaking Club',
            'Writing Club',
            'Cloud and DevOps',
            'Machine Learning',
            'Interview Preparation',
            'Others',
            'Tech Talk',
          ],
        },
        link: { ...linkSchema.definitions.linkSchema },
        icon: {
          type: 'string',
          enum: ['book_2', 'calendar_month', 'code_blocks', 'diversity_2', 'group', 'work'],
        },
      },
      additionalProperties: false,
      required: ['name', 'link', 'icon'],
    },
  },
};
