export const eventsfiltersSchema = {
  type: 'object',
  properties: {
    title: {
      type: 'string',
      minLength: 1,
    },
    filters: {
      type: 'object',
      properties: {
        type: {
          type: 'array',
          items: {
            type: 'string',
            enum: ['IN_PERSON', 'ONLINE_MEETUP', 'HYBRID'],
          },
        },
        topics: {
          type: 'array',
          items: {
            type: 'string',
            enum: [
              'Book Club',
              'Coding Club',
              'Python',
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
        },
        date: {
          type: 'array',
          items: {
            type: 'string',
            enum: ['IN_30_DAYS', 'IN_30_TO_60_DAYS', 'MORE_THAN_60_DAYS'],
          },
        },
        location: {
          type: 'array',
        },
      },
      additionalProperties: false,
      required: ['type', 'topics', 'date', 'location'],
    },
  },
  additionalProperties: false,
  required: ['title', 'filters'],
};
