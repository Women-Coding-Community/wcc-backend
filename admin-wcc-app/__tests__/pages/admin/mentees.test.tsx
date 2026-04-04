import '@testing-library/jest-dom';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import MenteesPage from '@/pages/admin/mentees/index';
import * as AuthProvider from '@/components/AuthProvider';
import * as menteeService from '@/services/menteeService';
import * as auth from '@/lib/auth';
import mockRouter from 'next-router-mock';

jest.mock('next/router', () => jest.requireActual('next-router-mock'));
jest.mock('@/components/AuthProvider');
jest.mock('@/services/menteeService');
jest.mock('@/lib/auth');

const mockUseAuth = AuthProvider.useAuth as jest.Mock;
const mockGetPending = menteeService.getPendingMenteeApplications as jest.Mock;
const mockApprove = menteeService.approveApplicationByAdmin as jest.Mock;
const mockReject = menteeService.rejectApplicationByAdmin as jest.Mock;
const mockGetStoredToken = auth.getStoredToken as jest.Mock;
const mockIsTokenExpired = auth.isTokenExpired as jest.Mock;

const sampleApplication = {
  applicationId: 1,
  menteeId: 42,
  mentorId: 10,
  cycleId: 3,
  priorityOrder: 1,
  status: 'PENDING',
  whyMentor: 'I want to learn from you',
  appliedAt: '2026-01-15T10:00:00Z',
  createdAt: '2026-01-15T10:00:00Z',
  updatedAt: '2026-01-15T10:00:00Z',
  reviewed: false,
  matched: false,
  daysSinceApplied: 5,
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
  });

  describe('Given user is ADMIN', () => {
    it('when loaded with pending applications, then they are displayed in a table', async () => {
      setupAuth(['ADMIN']);
      mockGetPending.mockResolvedValue([sampleApplication]);

      render(<MenteesPage />);

      await waitFor(() => {
        expect(screen.getByText('1')).toBeInTheDocument();
        expect(screen.getByText('42')).toBeInTheDocument();
        expect(screen.getByText('I want to learn from you')).toBeInTheDocument();
      });
    });

    it('when there are no pending applications, then empty state message is shown', async () => {
      setupAuth(['ADMIN']);
      mockGetPending.mockResolvedValue([]);

      render(<MenteesPage />);

      await waitFor(() => {
        expect(screen.getByText(/no pending mentee applications/i)).toBeInTheDocument();
      });
    });

    it('when Approve is clicked, then approveApplicationByAdmin is called', async () => {
      const user = userEvent.setup();
      setupAuth(['ADMIN']);
      mockGetPending.mockResolvedValue([sampleApplication]);
      mockApprove.mockResolvedValue({ ...sampleApplication, status: 'MENTOR_REVIEWING' });
      mockGetPending.mockResolvedValueOnce([sampleApplication]).mockResolvedValue([]);

      render(<MenteesPage />);

      const approveBtn = await screen.findByRole('button', { name: /approve/i });
      await user.click(approveBtn);

      await waitFor(() => {
        expect(mockApprove).toHaveBeenCalledWith(1, 'mock-token');
      });
    });

    it('when Reject is clicked and reason entered, then rejectApplicationByAdmin is called', async () => {
      const user = userEvent.setup();
      setupAuth(['ADMIN']);
      mockGetPending.mockResolvedValue([sampleApplication]);
      mockReject.mockResolvedValue({ ...sampleApplication, status: 'REJECTED' });
      mockGetPending.mockResolvedValueOnce([sampleApplication]).mockResolvedValue([]);

      render(<MenteesPage />);

      const rejectBtn = await screen.findByRole('button', { name: /^reject$/i });
      await user.click(rejectBtn);

      const reasonField = await screen.findByLabelText(/reason for rejection/i);
      await user.type(reasonField, 'Not a good fit');

      const confirmBtn = screen.getByRole('button', { name: /confirm reject/i });
      await user.click(confirmBtn);

      await waitFor(() => {
        expect(mockReject).toHaveBeenCalledWith(1, 'Not a good fit', 'mock-token');
      });
    });

    it('when API fails, then error message is displayed', async () => {
      setupAuth(['ADMIN']);
      mockGetPending.mockRejectedValue(new Error('Server error'));

      render(<MenteesPage />);

      await waitFor(() => {
        expect(screen.getByText('Server error')).toBeInTheDocument();
      });
    });
  });

  describe('Given user is MENTORSHIP_ADMIN', () => {
    it('when loaded, then page is accessible and shows applications', async () => {
      setupAuth(['MENTORSHIP_ADMIN']);
      mockGetPending.mockResolvedValue([sampleApplication]);

      render(<MenteesPage />);

      await waitFor(() => {
        expect(screen.getByText(/pending mentee applications/i)).toBeInTheDocument();
      });
    });
  });

  describe('Given user has no admin role', () => {
    it('when loaded with MENTOR role, then redirects to /admin', async () => {
      setupAuth(['MENTOR']);
      mockGetPending.mockResolvedValue([]);

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
