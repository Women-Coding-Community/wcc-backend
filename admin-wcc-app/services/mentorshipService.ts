import {apiFetch} from '@/lib/api';
import {MenteeApplicationItem, MentorshipRecommendationResponse} from '@/types/mentorship';

const MENTORSHIP_ADMIN_PATH = '/api/platform/v1/admin/mentorship';
const MENTEES_PATH = '/api/platform/v1/mentees';

export async function getMentorshipRecommendations(
    token: string
): Promise<MentorshipRecommendationResponse> {
  return apiFetch<MentorshipRecommendationResponse>(
      `${MENTORSHIP_ADMIN_PATH}/matches/recommendations`,
      {token}
  );
}

export async function getMenteeApplications(
    cycleId: number,
    statuses: string[],
    token: string,
    mentorId?: number
): Promise<MenteeApplicationItem[]> {
  const params = new URLSearchParams({status: statuses.join(',')});
  if (mentorId !== undefined) {
    params.append('mentorId', String(mentorId));
  }
  return apiFetch<MenteeApplicationItem[]>(
      `${MENTORSHIP_ADMIN_PATH}/cycles/${cycleId}/applications?${params.toString()}`,
      {token}
  );
}

export async function createManualMatch(
    menteeId: number | string,
    cycleId: number | string,
    mentorId: number | string,
    token: string,
    notes?: string
): Promise<void> {
  return apiFetch<void>(`${MENTEES_PATH}/${menteeId}/cycles/${cycleId}/assign-mentor`, {
    method: 'POST',
    body: {mentorId, notes},
    token,
  });
}
