import { createManualMatch, getMentorshipRecommendations } from '../../services/mentorshipService';
import { apiFetch } from '../../lib/api';

jest.mock('../../lib/api', () => ({
  apiFetch: jest.fn(),
}));

describe('mentorshipService', () => {
  const token = 'fake-token';

  afterEach(() => {
    jest.clearAllMocks();
  });

  describe('getMentorshipRecommendations', () => {
    it('should fetch mentorship recommendations for a program', async () => {
      const mockResponse = {
        matchedMentors: [],
        notMatchedMentors: [],
        notMatchedMentees: [],
      };
      (apiFetch as jest.Mock).mockResolvedValue(mockResponse);

      const result = await getMentorshipRecommendations(1);

      expect(apiFetch).toHaveBeenCalledWith(
        '/api/platform/v1/admin/mentorship/matches/recommendations/1',
        { token }
      );
      expect(result).toEqual(mockResponse);
    });
  });

  describe('createManualMatch', () => {
    it('should send a POST request to assign a mentor to a mentee', async () => {
      (apiFetch as jest.Mock).mockResolvedValue(undefined);

      await createManualMatch(20, 5, 10, token);

      expect(apiFetch).toHaveBeenCalledWith('/api/platform/v1/mentees/20/cycles/5/assign-mentor', {
        method: 'POST',
        body: { mentorId: 10, notes: undefined },
        token,
      });
    });

    it('should include notes in the request body when provided', async () => {
      (apiFetch as jest.Mock).mockResolvedValue(undefined);

      await createManualMatch(20, 5, 10, token, 'Good match for React skills');

      expect(apiFetch).toHaveBeenCalledWith('/api/platform/v1/mentees/20/cycles/5/assign-mentor', {
        method: 'POST',
        body: { mentorId: 10, notes: 'Good match for React skills' },
        token,
      });
    });
  });
});
