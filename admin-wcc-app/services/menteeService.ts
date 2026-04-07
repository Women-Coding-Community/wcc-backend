import { apiFetch } from '@/lib/api';
import {
  DashboardMentee,
  MenteeApplication,
  MenteeApplicationReview,
} from '@/types/menteeApplication';

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

export async function getPendingMenteeApplications(token: string): Promise<MenteeApplication[]> {
  return apiFetch<MenteeApplication[]>(`${BASE}/applications?status=PENDING`, { token });
}

export async function approveApplicationByAdmin(
  applicationId: number,
  token: string
): Promise<MenteeApplication> {
  return apiFetch<MenteeApplication>(`${BASE}/mentees/applications/${applicationId}/approve`, {
    method: 'PATCH',
    token,
  });
}

export async function rejectApplicationByAdmin(
  applicationId: number,
  reason: string,
  token: string
): Promise<MenteeApplication> {
  return apiFetch<MenteeApplication>(`${BASE}/mentees/applications/${applicationId}/reject`, {
    method: 'PATCH',
    body: { reason },
    token,
  });
}

export async function getPendingPriorityOneReviews(
  token: string
): Promise<MenteeApplicationReview[]> {
  return apiFetch<MenteeApplicationReview[]>(`${BASE}/mentees/applications/review`, { token });
}

export async function approveMenteeByMenteeId(
  menteeId: number,
  token: string
): Promise<MenteeApplication> {
  return apiFetch<MenteeApplication>(`${BASE}/mentees/${menteeId}/approve`, {
    method: 'PATCH',
    token,
  });
}

export async function rejectMenteeByMenteeId(
  menteeId: number,
  reason: string,
  token: string
): Promise<DashboardMentee> {
  return apiFetch<DashboardMentee>(`${BASE}/mentees/${menteeId}/reject`, {
    method: 'PATCH',
    body: { reason },
    token,
  });
}

export async function getPendingMentees(token: string): Promise<DashboardMentee[]> {
  return apiFetch<DashboardMentee[]>(`${BASE}/mentees/pending`, { token });
}

export async function getActiveMentees(token: string): Promise<DashboardMentee[]> {
  return apiFetch<DashboardMentee[]>(`${BASE}/mentees`, { token });
}

export async function activateMentee(menteeId: number, token: string): Promise<DashboardMentee> {
  return apiFetch<DashboardMentee>(`${BASE}/mentees/${menteeId}/activate`, {
    method: 'PATCH',
    token,
  });
}
