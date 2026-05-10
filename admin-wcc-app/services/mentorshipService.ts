import { apiFetch } from '@/lib/api';
import { MentorshipRecommendationResponse } from '@/types/mentorship';

const MENTORSHIP_ADMIN_PATH = '/api/platform/v1/admin/mentorship';
const MENTEES_PATH = '/api/platform/v1/mentees';

export async function getMentorshipRecommendations(
  token: string
): Promise<MentorshipRecommendationResponse> {
  const CYCLE_ID = 1; // TODO - Change to pickup the current cycle

  return apiFetch<MentorshipRecommendationResponse>(
    `${MENTORSHIP_ADMIN_PATH}/matches/recommendations/${CYCLE_ID}`,
    { token }
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
    body: { mentorId, notes },
    token,
  });
}
