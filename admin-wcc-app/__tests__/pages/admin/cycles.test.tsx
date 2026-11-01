import '@testing-library/jest-dom';
import { render, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import CyclesPage from '@/pages/admin/mentorship/cycles';
import * as AuthProvider from '@/components/AuthProvider';
import * as mentorshipService from '@/services/mentorshipService';
import * as auth from '@/lib/auth';
import mockRouter from 'next-router-mock';

jest.mock('next/router', () => jest.requireActual('next-router-mock'));
jest.mock('@/components/AuthProvider');
jest.mock('@/services/mentorshipService');
jest.mock('@/lib/auth');

const mockUseAuth = AuthProvider.useAuth as jest.Mock;
const mockGetCycles = mentorshipService.getCycles as jest.Mock;
const mockUpdateCycleStatus = mentorshipService.updateCycleStatus as jest.Mock;
const mockGetStoredToken = auth.getStoredToken as jest.Mock;
const mockIsTokenExpired = auth.isTokenExpired as jest.Mock;

const longTermCycle = {
  cycleId: 1,
  cycleYear: 2026,
  mentorshipType: 'Long-Term',
  cycleMonth: 'MARCH',
  registrationStartDate: '2026-09-30',
  registrationEndDate: '2026-11-06',
  cycleStartDate: '2026-11-07',
  cycleEndDate: '2027-05-05',
  status: 'OPEN',
};

const adHocCycle = {
  cycleId: 2,
  cycleYear: 2026,
  mentorshipType: 'Ad-Hoc',
  cycleMonth: 'MAY',
  registrationStartDate: '2026-05-01',
  registrationEndDate: '2026-05-10',
  cycleStartDate: '2026-05-15',
  cycleEndDate: '2026-05-31',
  status: 'DRAFT',
};

function setupAuth(roles: string[]) {
  mockGetStoredToken.mockReturnValue('mock-token');
  mockIsTokenExpired.mockReturnValue(false);
  mockUseAuth.mockReturnValue({ token: 'mock-token', roles, member: null, logout: jest.fn() });
}

async function pickStatus(cycleName: string, status: string) {
  const user = userEvent.setup();
  await user.click(await screen.findByRole('combobox', { name: `Change status of ${cycleName}` }));
  await user.click(screen.getByRole('option', { name: status }));
  return user;
}

describe('CyclesPage', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockRouter.setCurrentUrl('/admin/mentorship/cycles');
    mockGetCycles.mockResolvedValue([longTermCycle, adHocCycle]);
  });

  describe('Given user is ADMIN', () => {
    it('when loaded, then cycles for the current year are shown', async () => {
      setupAuth(['ADMIN']);

      render(<CyclesPage />);

      await waitFor(() => {
        expect(screen.getByText('March 2026')).toBeInTheDocument();
        expect(screen.getByText('May 2026')).toBeInTheDocument();
        expect(screen.getByText('OPEN')).toBeInTheDocument();
      });
      expect(mockGetCycles).toHaveBeenCalledWith(new Date().getFullYear(), 'mock-token');
    });

    it('when filtered by type, then only cycles of that type are shown', async () => {
      const user = userEvent.setup();
      setupAuth(['ADMIN']);

      render(<CyclesPage />);

      await screen.findByText('March 2026');
      await user.click(screen.getByRole('combobox', { name: 'Type' }));
      await user.click(screen.getByRole('option', { name: 'Ad-Hoc' }));

      expect(screen.getByText('May 2026')).toBeInTheDocument();
      expect(screen.queryByText('March 2026')).not.toBeInTheDocument();
    });

    it('when filtered by status, then only cycles with that status are shown', async () => {
      const user = userEvent.setup();
      setupAuth(['ADMIN']);

      render(<CyclesPage />);

      await screen.findByText('March 2026');
      await user.click(screen.getByRole('combobox', { name: 'Status' }));
      await user.click(screen.getByRole('option', { name: 'Open' }));

      expect(screen.getByText('March 2026')).toBeInTheDocument();
      expect(screen.queryByText('May 2026')).not.toBeInTheDocument();
    });

    it('when there are no cycles for the year, then empty state message is shown', async () => {
      setupAuth(['ADMIN']);
      mockGetCycles.mockResolvedValue([]);

      render(<CyclesPage />);

      await waitFor(() => {
        expect(screen.getByText(`No cycles for ${new Date().getFullYear()}.`)).toBeInTheDocument();
      });
    });

    it('when no cycles match the filters, then empty state message is shown', async () => {
      const user = userEvent.setup();
      setupAuth(['ADMIN']);

      render(<CyclesPage />);

      await screen.findByText('March 2026');
      await user.click(screen.getByRole('combobox', { name: 'Status' }));
      await user.click(screen.getByRole('option', { name: 'Completed' }));

      expect(screen.getByText(/no cycles match these filters/i)).toBeInTheDocument();
    });

    it('when a new status is picked, then a dialog asks to confirm the change', async () => {
      setupAuth(['ADMIN']);

      render(<CyclesPage />);

      await pickStatus('March 2026', 'Closed');

      const dialog = await screen.findByRole('dialog');
      expect(within(dialog).getByText('March 2026 · Long-Term')).toBeInTheDocument();
      expect(within(dialog).getByText('OPEN')).toBeInTheDocument();
      expect(within(dialog).getByText('CLOSED')).toBeInTheDocument();
    });

    it('when the change is confirmed, then the cycle is updated and a success message is shown', async () => {
      setupAuth(['ADMIN']);
      mockUpdateCycleStatus.mockResolvedValue({ ...longTermCycle, status: 'CLOSED' });

      render(<CyclesPage />);

      const user = await pickStatus('March 2026', 'Closed');
      await user.click(await screen.findByRole('button', { name: /confirm/i }));

      await waitFor(() => {
        expect(mockUpdateCycleStatus).toHaveBeenCalledWith(1, 'CLOSED', 'mock-token');
        expect(screen.getByText('March 2026 · Long-Term is now CLOSED.')).toBeInTheDocument();
      });
    });

    it('when the change is cancelled, then the cycle is not updated', async () => {
      setupAuth(['ADMIN']);

      render(<CyclesPage />);

      const user = await pickStatus('March 2026', 'Closed');
      await user.click(await screen.findByRole('button', { name: /cancel/i }));

      await waitFor(() => {
        expect(screen.queryByRole('dialog')).not.toBeInTheDocument();
      });
      expect(mockUpdateCycleStatus).not.toHaveBeenCalled();
    });

    it('when the update fails, then error message is displayed', async () => {
      setupAuth(['ADMIN']);
      mockUpdateCycleStatus.mockRejectedValue(
        new Error("Invalid status transition from 'DRAFT' to 'COMPLETED'")
      );

      render(<CyclesPage />);

      const user = await pickStatus('May 2026', 'Completed');
      await user.click(await screen.findByRole('button', { name: /confirm/i }));

      await waitFor(() => {
        expect(
          screen.getByText("Invalid status transition from 'DRAFT' to 'COMPLETED'")
        ).toBeInTheDocument();
      });
    });

    it('when API fails on load, then error message is displayed', async () => {
      setupAuth(['ADMIN']);
      mockGetCycles.mockRejectedValue(new Error('Server error'));

      render(<CyclesPage />);

      await waitFor(() => {
        expect(screen.getByText('Server error')).toBeInTheDocument();
      });
    });
  });

  describe('Given user is MENTORSHIP_ADMIN', () => {
    it('when loaded, then page is accessible and shows the cycles', async () => {
      setupAuth(['MENTORSHIP_ADMIN']);

      render(<CyclesPage />);

      await waitFor(() => {
        expect(screen.getByText('March 2026')).toBeInTheDocument();
      });
    });
  });

  describe('Given user has no admin role', () => {
    it('when loaded with MENTOR role, then redirects to /admin', async () => {
      setupAuth(['MENTOR']);

      render(<CyclesPage />);

      await waitFor(() => {
        expect(mockRouter.pathname).toBe('/admin');
      });
    });

    it('when token is missing, then redirects to /login', async () => {
      mockGetStoredToken.mockReturnValue(null);
      mockIsTokenExpired.mockReturnValue(false);
      mockUseAuth.mockReturnValue({ token: null, roles: [], member: null, logout: jest.fn() });

      render(<CyclesPage />);

      await waitFor(() => {
        expect(mockRouter.pathname).toBe('/login');
      });
    });
  });
});
