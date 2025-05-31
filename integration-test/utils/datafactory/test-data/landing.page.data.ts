export const landingPageData = {
  id: 'page:LANDING_PAGE',
  heroSection: {
    title: 'Women Coding Community',
    subtitle: 'Empowering Women in Their Tech Careers',
    images: [
      {
        path: 'https://drive.google.com/uc?id=1rzQtZnlyPHxdWHI6lS8bd0UeMkJnh7HF&export=download',
        alt: 'There are two women talking during a productive mentoring session',
        type: 'desktop',
      },
    ],
    customStyle: {
      backgroundColour: {
        color: 'primary',
        shade: {
          name: 'light',
          value: 90,
        },
      },
    },
  },
  fullBannerSection: {
    title: 'Become a Mentor',
    description:
      'Ready to empower and be empowered in tech? Become a mentor! Expand your network, give back, share expertise, and discover new perspectives.\n',
    link: {
      label: 'Join as a mentor',
      uri: '/mentorship/mentor-registration',
    },
    images: [
      {
        path: 'https://drive.google.com/uc?id=1efbBcw8yaASbSx3pgqcj06tIN-P2Wf55&export=download',
        alt: 'There are two women talking during a productive mentoring session',
        type: 'desktop',
      },
    ],
  },
  programmes: {
    title: 'Opportunities and Programmes',
    description:
      "Join our community and unlock endless opportunities. Network, find mentors, and access leadership programs. Whether you're aiming to enhance your skills, grow your professional network, or advance your career, we have what you need. We offer a wide range of opportunities to help you achieve your goals.",
    items: [
      {
        name: 'Others',
        link: {
          label: 'mentorship link',
          uri: '/mentorship',
        },
        icon: 'diversity_2',
      },
      {
        name: 'Others',
        link: {
          label: 'events',
          uri: '/events',
        },
        icon: 'calendar_month',
      },
      {
        name: 'Book Club',
        link: {
          uri: '/programmes/book-club',
        },
        icon: 'book_2',
      },
      {
        name: 'Others',
        link: {
          uri: '/programmes/cv-clinic',
        },
        icon: 'work',
      },
      {
        name: 'Others',
        link: {
          uri: '/programmes/interviews',
        },
        icon: 'group',
      },
      {
        name: 'Others',
        link: {
          uri: '/programmes/leetcode',
        },
        icon: 'code_blocks',
      },
    ],
  },
  announcements: {
    title: 'Announcements',
    items: [
      {
        title: 'Book Club: The Pragmatic Programmer',
        description: 'Join us for a discussion of this essential guide to writing clear and maintainable code!',
        eventType: 'IN_PERSON',
        startDate: 'Thu, May 30, 2024, 8:00 PM CEST',
        endDate: 'Thu, May 30, 2024, 9:30 PM CEST',
        topics: 'Book Club',
        images: [
          {
            path: 'https://secure.meetupstatic.com/photos/event/7/d/a/6/600_520892166.webp',
            alt: 'Pragmatic programmer book cover',
            type: 'desktop',
          },
        ],
        speakerProfile: {
          label: 'Jane Doe Profile',
          uri: 'https://linkedin',
        },
        hostProfile: {
          label: 'Host Profile',
          uri: 'https://linkedin.host',
        },
        eventLink: {
          label: 'Go to Meetup Event',
          uri: 'https://meetup.com/event1',
        },
      },
    ],
  },
  events: {
    title: 'Events',
    link: {
      label: 'View all events',
      uri: '/events',
    },
    items: [
      {
        title: 'Kedro:An',
        description:
          "Kedro is an open-source Python framework to create reproducible, maintainable, and modular data science code. It uses software engineering best practices to help you build production-ready data science pipelines. This presentation introduces Kedro's core features, including its application of software engineering principles to machine learning projects, seamless transition from development to production, and standardisation for efficient collaboration. We'll also look at a demo of what a Kedro project looks like through our visualisation plugin Kedro-Viz.",
        eventType: 'IN_PERSON',
        startDate: 'Thu, May 30, 2024, 8:00 PM CEST',
        endDate: 'Thu, May 30, 2024, 9:30 PM CEST',
        topics: 'Book Club',
        images: [
          {
            path: 'https://secure.meetupstatic.com/photos/event/6/4/c/e/600_522085806.webp',
            alt: 'There are two women talking during a productive mentoring session',
            type: 'desktop',
          },
        ],
        speakerProfile: {
          label: 'Merel Theisen',
          uri: 'https://www.linkedin.com/in/merel-theisen/',
        },
        hostProfile: {
          label: 'Silke Nodwell',
          uri: 'https://www.linkedin.com/in/silke-nodwell-763681172/',
        },
        eventLink: {
          label: 'Send us a report on Github',
          uri: 'https://github.com/Women-Coding-Community/wcc-frontend/issues',
        },
      },
      {
        title: 'Coding Club with Women Coding Community',
        description: 'We are excited to announce our next Coding Club session as a part of Women Coding Community!',
        eventType: 'ONLINE_MEETUP',
        startDate: 'Sun, Jun 30, 2024, 8:00 PM CEST',
        endDate: 'Sun, Jun 30, 2024, 9:30 PM CEST',
        topics: 'Tech Talk',
        images: [
          {
            path: 'https://secure.meetupstatic.com/photos/event/1/7/b/a/600_522666074.webp?w=750',
            alt: 'Event banner with events details',
            type: 'desktop',
          },
        ],
        speakerProfile: {
          label: 'Nonna Shakhova',
          uri: 'https://www.linkedin.com/in/nonna-shakhova',
        },
        hostProfile: {
          label: 'Irina Kamalova',
          uri: 'https://linkedin.com/in/irina-kamalova',
        },
        eventLink: {
          label: 'Go to Meetup event',
          uri: 'https://www.meetup.com/women-coding-community/events/302579035/',
        },
      },
    ],
  },
  volunteerSection: {
    title: 'Do you want to volunteer with us?',
    description:
      'Empowering women in their tech careers through education, mentorship, community building, and career services is our mission. We provide workshops and events, connect members with industry mentors, foster a supportive community through meetups and conferences, and raise awareness for more inclusive industry practices.',
    link: {
      title: 'Learn more about volunteering',
      uri: '/about-us/volunteer',
    },
    images: [
      {
        path: 'https://drive.google.com/uc?id=1fWzte4q2adiMf7MFAjMRlNDbccZVs7iL&export=download',
        alt: 'There are two women talking during a productive mentoring session',
        type: 'desktop',
      },
    ],
  },
};
