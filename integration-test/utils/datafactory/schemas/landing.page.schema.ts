import { heroSectionSchema } from './hero.section.schema';
import { commonsectionSchema } from './commonsection.schema';
import { listSectionProgrammeItemSchema } from './list.section.programme.item.schema';
import { listSectionEventSchema } from './list.section.event.schema';

export const landingPageSchema = {
  type: 'object',
  properties: {
    id: {
      type: 'string',
      const: 'page:LANDING_PAGE',
    },
    heroSection: { ...heroSectionSchema.definitions.heroSectionSchema },
    fullBannerSection: { ...commonsectionSchema.definitions.commonsectionSchema },
    programmes: { ...listSectionProgrammeItemSchema.definitions.listSectionProgrammeItemSchema },
    announcements: { ...listSectionEventSchema.definitions.listSectionEventSchema },
    events: { ...listSectionEventSchema.definitions.listSectionEventSchema },
    volunteerSection: { ...commonsectionSchema.definitions.commonsectionSchema },
  },
  required: ['id', 'heroSection', 'fullBannerSection', 'programmes', 'volunteerSection'],
};
