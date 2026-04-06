import { apiFetch } from '@/lib/api';
import { DashboardMentee, MenteeApplication } from '@/types/menteeApplication';

const BASE = '/api/platform/v1';

export async function getMentorApplications(
  mentorId: number,
  token: string,
  status?: string
): Promise<MenteeApplication[]> {
  const query = status ? `?status=${status}` : '';
  return apiFetch<MenteeApplication[]>(`${BASE}/mentors/${mentorId}/applications${query}`, {
    token,
  });
}

export async function acceptApplication(
  applicationId: number,
  token: string,
  mentorResponse?: string
): Promise<MenteeApplication> {
  return apiFetch<MenteeApplication>(`${BASE}/mentors/applications/${applicationId}/accept`, {
    method: 'PATCH',
    body: { mentorResponse },
    token,
  });
}

export async function declineApplication(
  applicationId: number,
  reason: string,
  token: string
): Promise<MenteeApplication> {
  return apiFetch<MenteeApplication>(`${BASE}/mentors/applications/${applicationId}/decline`, {
    method: 'PATCH',
    body: { reason },
    token,
  });
}

export async function getMentees(token: string): Promise<DashboardMentee[]> {
  return apiFetch<DashboardMentee[]>(`${BASE}/mentees`, { token });
}
