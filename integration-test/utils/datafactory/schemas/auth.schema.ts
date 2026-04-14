import { z } from 'zod';
import { memberDtoSchema } from './member.dto.schema';

export const roleTypeSchema = z.enum(['ADMIN', 'LEADER', 'MENTEE', 'MENTOR', 'CONTRIBUTOR', 'VIEWER']);

export const loginResponseSchema = z.object({
  token: z.string().min(1),
  expiresAt: z.string().min(1),
  roles: z.array(roleTypeSchema).min(1),
  member: memberDtoSchema.optional().nullable(),
  message: z.string().optional().nullable(),
});
