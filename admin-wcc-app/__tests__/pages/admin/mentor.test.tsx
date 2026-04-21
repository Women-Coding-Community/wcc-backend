import '@testing-library/jest-dom';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import MentorDashboardPage from '@/pages/admin/mentor';
import * as AuthProvider from '@/components/AuthProvider';
import * as menteeService from '@/services/menteeService';
import * as auth from '@/lib/auth';
import mockRouter from 'next-router-mock';

jest.mock('next/router', () => jest.requireActual('next-router-mock'));
jest.mock('@/components/AuthProvider');
jest.mock('@/services/menteeService');
jest.mock('@/lib/auth');

const mockUseAuth = AuthProvider.useAuth as jest.Mock;
const mockGetApplications = menteeService.getMentorApplications as jest.Mock;
const mockAccept = menteeService.acceptApplication as jest.Mock;
const mockDecline = menteeService.declineApplication as jest.Mock;
const mockGetStoredToken = auth.getStoredToken as jest.Mock;
const mockIsTokenExpired = auth.isTokenExpired as jest.Mock;

const matchedApp = {
  applicationId: 10,
  menteeId: 5,
  mentorId: 1,
  cycleId: 3,
  priorityOrder: 1,
  status: 'MATCHED',
  whyMentor: 'Great mentor',
  appliedAt: '2026-01-10T10:00:00Z',
  createdAt: '2026-01-10T10:00:00Z',
  updatedAt: '2026-01-10T10:00:00Z',
  reviewed: true,
  matched: true,
  daysSinceApplied: 10,
};

const reviewingApp = {
  applicationId: 20,
  menteeId: 7,
  mentorId: 1,
  cycleId: 3,
  priorityOrder: 1,
  status: 'MENTOR_REVIEWING',
  whyMentor: 'I need help with my career',
  appliedAt: '2026-02-01T10:00:00Z',
  createdAt: '2026-02-01T10:00:00Z',
  updatedAt: '2026-02-01T10:00:00Z',
  reviewed: false,
  matched: false,
  daysSinceApplied: 3,
};

const pendingApp = {
  ...reviewingApp,
  applicationId: 30,
  status: 'PENDING',
};

function setupAuth(roles: string[], mentorId = 1) {
  mockGetStoredToken.mockReturnValue('mock-token');
  mockIsTokenExpired.mockReturnValue(false);
  mockUseAuth.mockReturnValue({
    token: 'mock-token',
    roles,
    member: { id: mentorId, fullName: 'Test Mentor' },
    logout: jest.fn(),
  });
}

describe('MentorDashboardPage', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockRouter.setCurrentUrl('/admin/mentor');
  });

  describe('Given user is MENTOR', () => {
    it('when loaded with matched application, then assigned mentee is shown', async () => {
      setupAuth(['MENTOR']);
      mockGetApplications.mockResolvedValue([matchedApp]);

      render(<MentorDashboardPage />);

      await waitFor(() => {
        expect(screen.getByText('Mentee #5')).toBeInTheDocument();
        expect(screen.getByText('Active')).toBeInTheDocument();
      });
    });

    it('when loaded with MENTOR_REVIEWING application, then it appears in applications table', async () => {
      setupAuth(['MENTOR']);
      mockGetApplications.mockResolvedValue([reviewingApp]);

      render(<MentorDashboardPage />);

      await waitFor(() => {
        expect(screen.getByText('Mentee #7')).toBeInTheDocument();
        expect(screen.getByText('I need help with my career')).toBeInTheDocument();
      });
    });

    it('when loaded with PENDING application, then it does NOT appear in applications table', async () => {
      setupAuth(['MENTOR']);
      mockGetApplications.mockResolvedValue([pendingApp]);

      render(<MentorDashboardPage />);

      await waitFor(() => {
        expect(screen.getByText(/no pending applications/i)).toBeInTheDocument();
      });
      expect(screen.queryByText('Mentee #7')).not.toBeInTheDocument();
    });

    it('when Accept is clicked, then acceptApplication is called', async () => {
      const user = userEvent.setup();
      setupAuth(['MENTOR']);
      mockGetApplications.mockResolvedValue([reviewingApp]);
      mockAccept.mockResolvedValue({ ...reviewingApp, status: 'MENTOR_ACCEPTED' });
      mockGetApplications.mockResolvedValueOnce([reviewingApp]).mockResolvedValue([]);

      render(<MentorDashboardPage />);

      const acceptBtn = await screen.findByRole('button', { name: /^accept$/i });
      await user.click(acceptBtn);

      await waitFor(() => {
        expect(mockAccept).toHaveBeenCalledWith(20, 'mock-token');
      });
    });

    it('when Decline is clicked and reason entered, then declineApplication is called', async () => {
      const user = userEvent.setup();
      setupAuth(['MENTOR']);
      mockGetApplications.mockResolvedValue([reviewingApp]);
      mockDecline.mockResolvedValue({ ...reviewingApp, status: 'MENTOR_DECLINED' });
      mockGetApplications.mockResolvedValueOnce([reviewingApp]).mockResolvedValue([]);

      render(<MentorDashboardPage />);

      const declineBtn = await screen.findByRole('button', { name: /^decline$/i });
      await user.click(declineBtn);

      const reasonField = await screen.findByLabelText(/reason for declining/i);
      await user.type(reasonField, 'Not the right match');

      const confirmBtn = screen.getByRole('button', { name: /confirm decline/i });
      await user.click(confirmBtn);

      await waitFor(() => {
        expect(mockDecline).toHaveBeenCalledWith(20, 'Not the right match', 'mock-token');
      });
    });

    it('when both sections are empty, then empty state messages are shown', async () => {
      setupAuth(['MENTOR']);
      mockGetApplications.mockResolvedValue([]);

      render(<MentorDashboardPage />);

      await waitFor(() => {
        expect(screen.getByText(/no assigned mentees yet/i)).toBeInTheDocument();
        expect(screen.getByText(/no pending applications/i)).toBeInTheDocument();
      });
    });
  });

  describe('Given user is ADMIN', () => {
    it('when loaded, then dashboard is accessible', async () => {
      setupAuth(['ADMIN']);
      mockGetApplications.mockResolvedValue([]);

      render(<MentorDashboardPage />);

      await waitFor(() => {
        expect(screen.getByText(/my assigned mentee/i)).toBeInTheDocument();
      });
    });
  });

  describe('Given user does not have MENTOR or ADMIN role', () => {
    it('when loaded with MENTORSHIP_ADMIN role, then redirects to /admin', async () => {
      setupAuth(['MENTORSHIP_ADMIN']);

      render(<MentorDashboardPage />);

      await waitFor(() => {
        expect(mockRouter.pathname).toBe('/admin');
      });
    });

    it('when token is missing, then redirects to /login', async () => {
      mockGetStoredToken.mockReturnValue(null);
      mockIsTokenExpired.mockReturnValue(false);
      mockUseAuth.mockReturnValue({ token: null, roles: [], member: null, logout: jest.fn() });

      render(<MentorDashboardPage />);

      await waitFor(() => {
        expect(mockRouter.pathname).toBe('/login');
      });
    });
  });
});
