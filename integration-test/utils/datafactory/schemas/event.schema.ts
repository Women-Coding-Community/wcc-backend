import { imagesSchema } from './images.schema';
import { linkSchema } from './link.schema';

export const eventSchema = {
  $ref: '#/definitions/eventSchema',
  definitions: {
    eventSchema: {
      type: 'object',
      properties: {
        id: {
          type: 'string',
          minLength: 1,
        },
        title: {
          type: 'string',
          minLength: 1,
        },
        description: {
          type: 'string',
          minLength: 1,
        },
        eventType: {
          type: 'string',
          minLength: 1,
          enum: ['IN_PERSON', 'ONLINE_MEETUP', 'HYBRID'],
        },
        startDate: {
          type: 'string',
          minLength: 1,
        },
        endDate: {
          type: 'string',
          minLength: 1,
        },
        topics: {
          type: 'string',
          minLength: 1,
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
        images: { ...imagesSchema.definitions.imagesSchema },
        speakerProfile: { ...linkSchema.definitions.linkSchema },
        hostProfile: { ...linkSchema.definitions.linkSchema },
        eventLink: { ...linkSchema.definitions.linkSchema },
        eventResources: {
          type: 'array',
          items: [
            {
              type: 'object',
              properties: {
                link: { ...linkSchema.definitions.linkSchema },
              },
            },
          ],
        },
      },
      additionalProperties: false,
      required: ['title', 'description', 'eventType', 'startDate', 'endDate', 'topics', 'images', 'eventLink'],
    },
  },
};
