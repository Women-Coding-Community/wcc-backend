import { z } from 'zod';
import { roleTypeSchema } from './auth.schema';

const permissionSchema = z.string().min(1);

export const userAccountSchema = z.object({
  id: z.number(),
  memberId: z.number().nullable(),
  email: z.string(),
  roles: z.array(roleTypeSchema),
  permissions: z.array(permissionSchema),
  enabled: z.boolean(),
}).strict();

export const usersResponseSchema = z.array(userAccountSchema);
