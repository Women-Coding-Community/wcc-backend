import {
  createManualMatch,
  getMenteeApplications,
  getMentorshipRecommendations,
} from '@/services/mentorshipService';
import {apiFetch} from '@/lib/api';

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

      const result = await getMentorshipRecommendations(token);

      expect(apiFetch).toHaveBeenCalledWith(
          '/api/platform/v1/admin/mentorship/matches/recommendations',
          {token}
      );
      expect(result).toEqual(mockResponse);
    });
  });

  describe('getMenteeApplications', () => {
    it('should fetch mentee applications without mentor filter', async () => {
      const mockApps = [{menteeId: 1, status: 'PENDING'}];
      (apiFetch as jest.Mock).mockResolvedValue(mockApps);

      const result = await getMenteeApplications(5, ['PENDING', 'ACCEPTED'], token);

      expect(apiFetch).toHaveBeenCalledWith(
          '/api/platform/v1/admin/mentorship/cycles/5/applications?status=PENDING%2CACCEPTED',
          {token}
      );
      expect(result).toEqual(mockApps);
    });

    it('should fetch mentee applications with mentor filter', async () => {
      (apiFetch as jest.Mock).mockResolvedValue([]);

      await getMenteeApplications(5, ['PENDING'], token, 10);

      expect(apiFetch).toHaveBeenCalledWith(
          '/api/platform/v1/admin/mentorship/cycles/5/applications?status=PENDING&mentorId=10',
          {token}
      );
    });
  });

  describe('createManualMatch', () => {
    it('should send a POST request to assign a mentor to a mentee', async () => {
      (apiFetch as jest.Mock).mockResolvedValue(undefined);

      await createManualMatch(20, 5, 10, token);

      expect(apiFetch).toHaveBeenCalledWith('/api/platform/v1/mentees/20/cycles/5/assign-mentor', {
        method: 'POST',
        body: {mentorId: 10, notes: undefined},
        token,
      });
    });

    it('should include notes in the request body when provided', async () => {
      (apiFetch as jest.Mock).mockResolvedValue(undefined);

      await createManualMatch(20, 5, 10, token, 'Good match for React skills');

      expect(apiFetch).toHaveBeenCalledWith('/api/platform/v1/mentees/20/cycles/5/assign-mentor', {
        method: 'POST',
        body: {mentorId: 10, notes: 'Good match for React skills'},
        token,
      });
    });
  });
});
