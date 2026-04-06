import { z } from 'zod';

const countrySchema = z
  .object({
    countryCode: z.string(),
    countryName: z.string(),
  })
  .nullable();

const monthAvailabilitySchema = z.object({
  month: z.string(),
  hours: z.number().min(0),
  enabled: z.boolean(),
});

const technicalAreaProficiencySchema = z.object({
  technicalArea: z.string(),
  proficiencyLevel: z.string(),
});

const languageProficiencySchema = z.object({
  language: z.string(),
  proficiencyLevel: z.string(),
});

const linkSchema = z.object({
  title: z.string().optional().default(''),
  label: z.string().optional().default(''),
  uri: z.string(),
});

export const editMentorSchema = z.object({
  fullName: z.string().min(1, 'Full name is required'),
  email: z.email('Invalid email format'),
  position: z.string().min(1, 'Position is required'),
  slackDisplayName: z.string().min(1, 'Slack display name is required'),
  companyName: z.string().optional().default(''),
  city: z.string().min(1, 'City is required'),
  country: countrySchema.refine((val) => val !== null, { message: 'Country is required' }),
  spokenLanguages: z.array(z.string()).min(1, 'At least one spoken language is required'),
  bio: z.string().min(1, 'Bio is required'),
  yearsExperience: z
    .union([z.number().min(0), z.literal('')])
    .refine((val) => val !== '', { message: 'Years of experience is required' }),
  technicalAreas: z
    .array(technicalAreaProficiencySchema)
    .min(1, 'At least one technical area is required'),
  programmingLanguages: z
    .array(languageProficiencySchema)
    .min(1, 'At least one programming language is required'),
  mentorshipFocus: z.array(z.string()).min(1, 'At least one mentorship focus area is required'),
  mentorshipType: z.array(z.string()).min(1, 'At least one mentorship type is required'),
  idealMentee: z.string().min(1, 'Ideal mentee description is required'),
  additionalInfo: z.string().optional().default(''),
  monthAvailability: z.array(monthAvailabilitySchema),
  books: z.string().optional().default(''),
  links: z.array(linkSchema).default([]),
  network: z
    .array(
      z.object({
        type: z.string(),
        link: z.string(),
      })
    )
    .default([]),
});

export type EditMentorFormData = z.input<typeof editMentorSchema>;
