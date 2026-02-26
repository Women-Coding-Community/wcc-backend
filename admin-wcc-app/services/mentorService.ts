import { apiFetch } from '@/lib/api';
import { MentorItem } from '@/types/mentor';

const MENTORS_PATH = '/api/platform/v1/mentors';

type MentorsResponse =
  | MentorItem[]
  | {
      items?: MentorItem[];
      content?: MentorItem[];
      data?: MentorItem[];
    };

function normalize(resp: MentorsResponse): MentorItem[] {
  if (Array.isArray(resp)) return resp;
  if ('items' in resp && Array.isArray(resp.items)) return resp.items;
  if ('content' in resp && Array.isArray(resp.content)) return resp.content;
  if ('data' in resp && Array.isArray(resp.data)) return resp.data;
  return [];
}

export async function getMentors(token: string): Promise<MentorItem[]> {
  const data = await apiFetch<MentorsResponse>(MENTORS_PATH, { token });
  return normalize(data);
}
