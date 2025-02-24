import { heroSectionSchema } from './hero.section.schema';
import { commonsectionSchema } from './commonsection.schema';
import { sectionProgrammeSchema } from './section.programme.schema';
import { sectionEventSchema } from './section.event.schema';

export const landingPageSchema = {
  type: 'object',
  properties: {
    id: {
      type: 'string',
      const: 'page:LANDING_PAGE',
    },
    heroSection: { ...heroSectionSchema.definitions.heroSectionSchema },
    fullBannerSection: { ...commonsectionSchema.definitions.commonsectionSchema },
    programmes: { ...sectionProgrammeSchema.definitions.sectionProgrammeSchema },
    announcements: { ...sectionEventSchema.definitions.sectionEventSchema },
    events: { ...sectionEventSchema.definitions.sectionEventSchema },
    volunteerSection: { ...commonsectionSchema.definitions.commonsectionSchema },
  },
  required: ['id', 'heroSection', 'fullBannerSection', 'programmes', 'volunteerSection'],
};
