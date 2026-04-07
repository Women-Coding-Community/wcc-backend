import '@testing-library/jest-dom';
import { render, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import MenteesPage from '@/pages/admin/mentees/index';
import * as AuthProvider from '@/components/AuthProvider';
import * as menteeService from '@/services/menteeService';
import * as auth from '@/lib/auth';
import mockRouter from 'next-router-mock';
import { SocialNetworkType } from '@/types/member';

jest.mock('next/router', () => jest.requireActual('next-router-mock'));
jest.mock('@/components/AuthProvider');
jest.mock('@/services/menteeService');
jest.mock('@/lib/auth');

const mockUseAuth = AuthProvider.useAuth as jest.Mock;
const mockGetPendingMentees = menteeService.getPendingMentees as jest.Mock;
const mockGetActiveMentees = menteeService.getActiveMentees as jest.Mock;
const mockActivateMentee = menteeService.activateMentee as jest.Mock;
const mockRejectMenteeByMenteeId = menteeService.rejectMenteeByMenteeId as jest.Mock;
const mockGetStoredToken = auth.getStoredToken as jest.Mock;
const mockIsTokenExpired = auth.isTokenExpired as jest.Mock;

const pendingMentee = {
  id: 42,
  fullName: 'Jane Doe',
  email: 'jane@wcc.com',
  position: 'Software Engineer',
  profileStatus: 'PENDING',
};

const activeMentee = {
  id: 10,
  fullName: 'Alice Smith',
  email: 'alice@wcc.com',
  position: 'Tech Lead',
  profileStatus: 'ACTIVE',
};

function setupAuth(roles: string[]) {
  mockGetStoredToken.mockReturnValue('mock-token');
  mockIsTokenExpired.mockReturnValue(false);
  mockUseAuth.mockReturnValue({ token: 'mock-token', roles, member: null, logout: jest.fn() });
}

describe('MenteesPage', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockRouter.setCurrentUrl('/admin/mentees');
    mockGetActiveMentees.mockResolvedValue([]);
  });

  describe('Given user is ADMIN', () => {
    it('when loaded with pending mentees, then they are shown in the pending table', async () => {
      setupAuth(['ADMIN']);
      mockGetPendingMentees.mockResolvedValue([pendingMentee]);

      render(<MenteesPage />);

      await waitFor(() => {
        expect(screen.getByText('Jane Doe')).toBeInTheDocument();
        expect(screen.getByText('jane@wcc.com')).toBeInTheDocument();
        expect(screen.getByText('PENDING')).toBeInTheDocument();
      });
    });

    it('when loaded with 1 pending mentee, then pending count badge shows 1', async () => {
      setupAuth(['ADMIN']);
      mockGetPendingMentees.mockResolvedValue([pendingMentee]);

      render(<MenteesPage />);

      await waitFor(() => {
        const headings = screen.getAllByText('1');
        expect(headings.length).toBeGreaterThanOrEqual(1);
      });
    });

    it('when loaded with 1 active mentee, then active count badge shows 1', async () => {
      setupAuth(['ADMIN']);
      mockGetPendingMentees.mockResolvedValue([]);
      mockGetActiveMentees.mockResolvedValue([activeMentee]);

      render(<MenteesPage />);

      await waitFor(() => {
        const badges = screen.getAllByText('1');
        expect(badges.length).toBeGreaterThanOrEqual(1);
      });
    });

    it('when there are no pending mentees, then empty state message is shown', async () => {
      setupAuth(['ADMIN']);
      mockGetPendingMentees.mockResolvedValue([]);

      render(<MenteesPage />);

      await waitFor(() => {
        expect(screen.getByText(/no pending mentees awaiting review/i)).toBeInTheDocument();
      });
    });

    it('when loaded with active mentees, then they are shown in the active table', async () => {
      setupAuth(['ADMIN']);
      mockGetPendingMentees.mockResolvedValue([]);
      mockGetActiveMentees.mockResolvedValue([activeMentee]);

      render(<MenteesPage />);

      await waitFor(() => {
        expect(screen.getByText('Alice Smith')).toBeInTheDocument();
        expect(screen.getByText('alice@wcc.com')).toBeInTheDocument();
        expect(screen.getByText('ACTIVE')).toBeInTheDocument();
      });
    });

    it('when mentee has linkedin network entry, then LinkedIn link is shown', async () => {
      setupAuth(['ADMIN']);
      mockGetPendingMentees.mockResolvedValue([
        {
          ...pendingMentee,
          network: [{ type: SocialNetworkType.LINKEDIN, link: 'https://linkedin.com/in/jane' }],
        },
      ]);

      render(<MenteesPage />);

      await waitFor(() => {
        const link = screen.getByRole('link', { name: /linkedin/i });
        expect(link).toBeInTheDocument();
        expect(link).toHaveAttribute('href', 'https://linkedin.com/in/jane');
      });
    });

    it('when mentee has no network, then LinkedIn link is not shown', async () => {
      setupAuth(['ADMIN']);
      mockGetPendingMentees.mockResolvedValue([{ ...pendingMentee, network: [] }]);

      render(<MenteesPage />);

      await waitFor(() => {
        expect(screen.queryByRole('link', { name: /linkedin/i })).not.toBeInTheDocument();
      });
    });

    it('when mentee network type is unknown, then LinkedIn link is not shown', async () => {
      setupAuth(['ADMIN']);
      mockGetPendingMentees.mockResolvedValue([
        { ...pendingMentee, network: [{ type: 'github', link: 'https://github.com/jane' }] },
      ]);

      render(<MenteesPage />);

      await waitFor(() => {
        expect(screen.queryByRole('link', { name: /linkedin/i })).not.toBeInTheDocument();
      });
    });

    it('when Activate is clicked, then activateMentee is called with mentee ID', async () => {
      const user = userEvent.setup();
      setupAuth(['ADMIN']);
      mockGetPendingMentees.mockResolvedValue([pendingMentee]);
      mockActivateMentee.mockResolvedValue({ ...pendingMentee, profileStatus: 'ACTIVE' });
      mockGetPendingMentees.mockResolvedValueOnce([pendingMentee]).mockResolvedValue([]);

      render(<MenteesPage />);

      const activateBtn = await screen.findByRole('button', { name: /activate/i });
      await user.click(activateBtn);

      await waitFor(() => {
        expect(mockActivateMentee).toHaveBeenCalledWith(42, 'mock-token');
      });
    });

    it('when Reject is clicked and reason entered, then rejectMenteeByMenteeId is called', async () => {
      const user = userEvent.setup();
      setupAuth(['ADMIN']);
      mockGetPendingMentees.mockResolvedValue([pendingMentee]);
      mockRejectMenteeByMenteeId.mockResolvedValue({ ...pendingMentee, profileStatus: 'REJECTED' });
      mockGetPendingMentees.mockResolvedValueOnce([pendingMentee]).mockResolvedValue([]);

      render(<MenteesPage />);

      const rejectBtn = await screen.findByRole('button', { name: /^reject$/i });
      await user.click(rejectBtn);

      const dialog = await screen.findByRole('dialog');
      expect(within(dialog).getByText('Jane Doe')).toBeInTheDocument();

      const reasonField = await screen.findByLabelText(/reason for rejection/i);
      await user.type(
        reasonField,
        'Does not meet the eligibility criteria for this mentorship cycle at this time.'
      );

      const confirmBtn = screen.getByRole('button', { name: /confirm reject/i });
      await user.click(confirmBtn);

      await waitFor(() => {
        expect(mockRejectMenteeByMenteeId).toHaveBeenCalledWith(
          42,
          'Does not meet the eligibility criteria for this mentorship cycle at this time.',
          'mock-token'
        );
      });
    });

    it('when API fails on load, then error message is displayed', async () => {
      setupAuth(['ADMIN']);
      mockGetPendingMentees.mockRejectedValue(new Error('Server error'));

      render(<MenteesPage />);

      await waitFor(() => {
        expect(screen.getByText('Server error')).toBeInTheDocument();
      });
    });
  });

  describe('Given user is MENTORSHIP_ADMIN', () => {
    it('when loaded, then page is accessible and shows pending section', async () => {
      setupAuth(['MENTORSHIP_ADMIN']);
      mockGetPendingMentees.mockResolvedValue([pendingMentee]);

      render(<MenteesPage />);

      await waitFor(() => {
        expect(screen.getByText(/pending mentees/i)).toBeInTheDocument();
      });
    });
  });

  describe('Given user has no admin role', () => {
    it('when loaded with MENTOR role, then redirects to /admin', async () => {
      setupAuth(['MENTOR']);
      mockGetPendingMentees.mockResolvedValue([]);

      render(<MenteesPage />);

      await waitFor(() => {
        expect(mockRouter.pathname).toBe('/admin');
      });
    });

    it('when token is missing, then redirects to /login', async () => {
      mockGetStoredToken.mockReturnValue(null);
      mockIsTokenExpired.mockReturnValue(false);
      mockUseAuth.mockReturnValue({ token: null, roles: [], member: null, logout: jest.fn() });

      render(<MenteesPage />);

      await waitFor(() => {
        expect(mockRouter.pathname).toBe('/login');
      });
    });
  });
});
