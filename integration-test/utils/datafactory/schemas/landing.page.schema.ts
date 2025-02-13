import { heroSectionSchema } from './hero.section.schema';
import { pageSchema } from './page.schema';
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
    fullBannerSection: { ...pageSchema.definitions.pageSchema },
    programmes: { ...sectionProgrammeSchema.definitions.sectionProgrammeSchema },
    announcements: { ...sectionEventSchema.definitions.sectionEventSchema },
    events: { ...sectionEventSchema.definitions.sectionEventSchema },
    volunteerSection: { ...pageSchema.definitions.pageSchema },
  },
  required: ['id', 'heroSection', 'fullBannerSection', 'programmes', 'volunteerSection'],
};
