import { z } from 'zod';
import { roleTypeSchema } from './auth.schema';

const permissionSchema = z.enum([
  'USER_READ',
  'USER_WRITE',
  'USER_DELETE',
  'MENTOR_APPL_READ',
  'MENTOR_APPL_WRITE',
  'MENTOR_PROFILE_UPDATE',
  'MENTEE_APPL_SUBMIT',
  'MENTEE_APPL_READ',
  'MENTOR_APPROVE',
  'MENTEE_APPROVE',
  'CYCLE_EMAIL_SEND',
  'MATCH_MANAGE',
]);

export const userAccountSchema = z.object({
  id: z.number(),
  memberId: z.number().nullable(),
  email: z.string(),
  roles: z.array(roleTypeSchema),
  permissions: z.array(permissionSchema),
  enabled: z.boolean(),
}).strict();

export const usersResponseSchema = z.array(userAccountSchema);
