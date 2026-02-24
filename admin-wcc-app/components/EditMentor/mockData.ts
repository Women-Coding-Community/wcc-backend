export const MOCK_MENTOR = {
  id: 1,
  fullName: 'Maya Anderson',
  email: 'maya@gmail.com',
  position: 'Fullstack Developer, Cloud Architect',
  slackDisplayName: 'mayaanderson',
  companyName: 'Google',
  city: 'London',
  country: { countryCode: 'GB', countryName: 'United Kingdom' },
  profileStatus: 'ACTIVE',
  bio: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.',
  spokenLanguages: ['English', 'French'],
  memberTypes: ['MENTOR'],
  skills: {
    yearsExperience: 5,
    areas: [
      { technicalArea: 'BACKEND', proficiencyLevel: 'EXPERT' },
      { technicalArea: 'FRONTEND', proficiencyLevel: 'ADVANCED' },
    ],
    languages: [
      { language: 'C_PLUS_PLUS', proficiencyLevel: 'ADVANCED' },
      { language: 'JAVA', proficiencyLevel: 'EXPERT' },
      { language: 'JAVASCRIPT', proficiencyLevel: 'ADVANCED' },
      { language: 'TYPESCRIPT', proficiencyLevel: 'ADVANCED' },
      { language: 'PYTHON', proficiencyLevel: 'INTERMEDIATE' },
    ],
    mentorshipFocus: ['SWITCH_CAREER_TO_IT', 'GROW_MID_TO_SENIOR'],
  },
  menteeSection: {
    mentorshipType: ['AD_HOC'],
    idealMentee:
      'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.',
    additional:
      'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.',
    adHoc: [
      { month: 'JANUARY', hours: 0 },
      { month: 'FEBRUARY', hours: 0 },
      { month: 'MARCH', hours: 0 },
      { month: 'APRIL', hours: 0 },
      { month: 'MAY', hours: 0 },
      { month: 'JUNE', hours: 0 },
      { month: 'JULY', hours: 0 },
      { month: 'AUGUST', hours: 0 },
      { month: 'SEPTEMBER', hours: 0 },
      { month: 'OCTOBER', hours: 0 },
      { month: 'NOVEMBER', hours: 0 },
      { month: 'DECEMBER', hours: 0 },
    ],
  },
  resources: {
    books:
      'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.',
    links: [] as { title: string; label: string; uri: string }[],
  },
  profilePictureUrl: 'https://i.pravatar.cc/150?img=47',
  images: [] as { path: string; alt: string; type: string }[],
  network: [] as { type: string; link: string }[],
};
