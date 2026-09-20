import { API_BASE, API_KEY, apiFetch } from '@/lib/api';
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

export async function getMentorById(
  mentorId: string | number,
  token: string
): Promise<MentorItem | null> {
  return apiFetch<MentorItem>(`${MENTORS_PATH}/${mentorId}`, { token });
}

export function formatDriveImageUrl(link?: string | null): string | null {
  if (!link) return null;
  const match = link.match(/\/file\/d\/([a-zA-Z0-9_-]+)/) || link.match(/[?&]id=([a-zA-Z0-9_-]+)/);
  if (match && match[1]) {
    return `https://drive.google.com/thumbnail?id=${match[1]}&sz=w400`;
  }
  return link;
}

export async function getMentorProfilePicture(
  mentorId: string | number,
  token: string
): Promise<string | null> {
  try {
    const data = await apiFetch<{ resource?: { driveFileLink?: string } }>(
      `/api/platform/v1/resources/member-profile-picture/${mentorId}`,
      { token }
    );
    const link = data?.resource?.driveFileLink ?? null;
    return formatDriveImageUrl(link);
  } catch {
    return null;
  }
}

export async function uploadMentorProfilePicture(
  mentorId: string | number,
  file: File,
  token: string
): Promise<string> {
  const formData = new FormData();
  formData.append('file', file);

  const headers: Record<string, string> = {
    Authorization: `Bearer ${token}`,
  };
  if (API_KEY) {
    headers['X-API-KEY'] = API_KEY;
  }

  const res = await fetch(
    `${API_BASE}/api/platform/v1/resources/member-profile-picture?memberId=${mentorId}`,
    {
      method: 'POST',
      headers,
      body: formData,
    }
  );

  if (!res.ok) {
    let message = `${res.status} ${res.statusText || 'Error'}`;
    try {
      const data = await res.json();
      if (data.message) message = data.message;
    } catch {}
    throw new Error(message);
  }

  const data = await res.json();
  const link = data.resource?.driveFileLink ?? '';
  return formatDriveImageUrl(link) || link;
}
