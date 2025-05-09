export const eventsPageData = {
  id: 'page:EVENTS',
  metadata: {
    pagination: {
      totalItems: 1,
      totalPages: 1,
      currentPage: 1,
      pageSize: 10,
    },
  },
  heroSection: {
    title: 'Online and In-Person Events',
    images: [
      {
        path: 'https://cloudprovider.com/image.png',
        alt: 'There is a group of women showing WCC logo',
        type: 'desktop',
      },
    ],
  },
  section: {
    description:
      'Join the Women Coding Community for events and meetups that enhance your skills, deepen your knowledge, and expand your professional network.\n\nOur events include webinars, hands-on workshops, study groups, panel discussions, and keynotes with industry experts. Whether online or in-person, these gatherings offer valuable opportunities to share insights, learn collaboratively, and stay updated on the latest trends. Connect, grow, and thrive with fellow members in a supportive environment.',
  },
  contact: {
    title: 'Contact us',
    links: [
      {
        type: 'email',
        link: 'london@womencodingcommunity.com',
      },
      {
        type: 'slack',
        link: 'https://join.slack.com/t/womencodingcommunity/shared_invite/zt-2hpjwpx7l-rgceYBIWp6pCiwc0hVsX8A',
      },
    ],
  },
  data: {
    items: [
      {
        topics: 'Book Club',
        eventType: 'IN_PERSON',
        startDate: 'Thu, May 30, 2024, 8:00 PM CEST',
        endDate: 'Thu, May 30, 2024, 9:30 PM CEST',
        title: 'Book Club: The Pragmatic Programmer',
        speakerProfile: {
          label: 'John Doe',
          uri: 'https://meetup.com/event1',
        },
        hostProfile: {
          label: 'John M',
          uri: 'https://meetup.com/event1',
        },
        description: 'Join us for a discussion of this essential guide to writing clear and maintainable code!',
        images: [
          {
            path: 'https://cloudprovider.com/image.png',
            alt: 'Pragmatic programmer book cover',
            type: 'desktop',
          },
        ],
        eventLink: {
          label: 'Go to Meetup Event',
          uri: 'https://meetup.com/event1',
        },
        eventResources: [
          {
            link: {
              label: 'Slides',
              uri: 'https://googledrive/event1',
            },
          },
          {
            link: {
              label: 'Recording',
              uri: 'https://meetup.com/event1',
            },
          },
        ],
      },
    ],
  },
};
