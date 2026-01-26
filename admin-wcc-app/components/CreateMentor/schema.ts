import { z } from 'zod';

const countrySchema = z
  .object({
    countryCode: z.string(),
    countryName: z.string(),
  })
  .nullable();

export const mentorSchema = z.object({
  fullName: z.string().min(1, 'Full name is required'),
  email: z.string().min(1, 'Email is required').email('Invalid email format'),
  position: z.string().min(1, 'Position is required'),
  slackDisplayName: z.string().min(1, 'Slack display name is required'),
  companyName: z.string().optional().default(''),
  city: z.string().optional().default(''),
  country: countrySchema.refine((val) => val !== null, { message: 'Country is required' }),
  memberTypes: z.array(z.string()).min(1, 'At least one member type is required'),
  profileStatus: z.string().min(1, 'Profile status is required'),
  bio: z.string().min(1, 'Bio is required'),
  spokenLanguages: z.array(z.string()).default([]),
  yearsExperience: z
    .union([z.number().min(0), z.literal('')])
    .refine((val) => val !== '', { message: 'Years of experience is required' }),
  technicalAreas: z.array(z.string()).min(1, 'At least one technical area is required'),
  programmingLanguages: z.array(z.string()).min(1, 'At least one programming language is required'),
  mentorshipFocus: z.array(z.string()).min(1, 'At least one mentorship focus area is required'),
  mentorshipType: z.array(z.string()).min(1, 'At least one mentorship type is required'),
  idealMentee: z.string().min(1, 'Ideal mentee description is required'),
  additionalInfo: z.string().optional().default(''),
  images: z
    .array(
      z.object({
        path: z.string(),
        alt: z.string(),
        type: z.string(),
      })
    )
    .default([]),
  network: z
    .array(
      z.object({
        type: z.string(),
        link: z.string(),
      })
    )
    .default([]),
});

export type MentorFormData = z.input<typeof mentorSchema>;
