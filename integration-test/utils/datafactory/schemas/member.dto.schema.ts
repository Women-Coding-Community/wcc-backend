import { z } from 'zod';

const memberTypeSchema = z.enum([
  'DIRECTOR',
  'COLLABORATOR',
  'EVANGELIST',
  'LEADER',
  'MENTEE',
  'MENTOR',
  'MEMBER',
  'PARTNER',
  'SPEAKER',
  'VOLUNTEER',
]);

const pronounCategorySchema = z.enum([
  'FEMININE',
  'MASCULINE',
  'NEUTRAL',
  'MULTIPLE',
  'NEOPRONOUNS',
  'ANY',
  'UNSPECIFIED',
]);

export const memberDtoSchema = z.object({
  id: z.number().optional().nullable(),
  fullName: z.string().min(1),
  position: z.string().min(1),
  email: z.string(),
  slackDisplayName: z.string().optional().nullable(),
  country: z
    .object({
      countryCode: z.string(),
      countryName: z.string(),
    })
    .optional()
    .nullable(),
  city: z.string().optional().nullable(),
  companyName: z.string().optional().nullable(),
  memberTypes: z.array(memberTypeSchema).optional().nullable(),
  images: z.array(z.record(z.string(), z.unknown())).optional().nullable(),
  network: z.array(z.record(z.string(), z.unknown())).optional().nullable(),
  pronouns: z.string().optional().nullable(),
  pronounCategory: pronounCategorySchema.optional().nullable(),
  isWomen: z.boolean().optional().nullable(),
});
