import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';
import AdminLayout from '@/components/AdminLayout';
import * as AuthProvider from '@/components/AuthProvider';

// eslint-disable-next-line @typescript-eslint/no-require-imports
jest.mock('next/router', () => require('next-router-mock'));
jest.mock('@/components/AuthProvider');

const mockUseAuth = AuthProvider.useAuth as jest.Mock;

function renderLayout(roles: string[]) {
  mockUseAuth.mockReturnValue({ logout: jest.fn(), roles, token: 'tok', member: null });
  render(<AdminLayout>content</AdminLayout>);
}

describe('AdminLayout', () => {
  beforeEach(() => jest.clearAllMocks());

  describe('Given user is ADMIN', () => {
    it('when rendered, then all nav items are visible', () => {
      renderLayout(['ADMIN']);
      expect(screen.getByRole('link', { name: /mentor dashboard/i })).toBeInTheDocument();
      expect(screen.getByRole('link', { name: /^mentors$/i })).toBeInTheDocument();
      expect(screen.getByRole('link', { name: /^members$/i })).toBeInTheDocument();
      expect(screen.getByRole('link', { name: /^mentees$/i })).toBeInTheDocument();
      expect(screen.getByRole('link', { name: /^users$/i })).toBeInTheDocument();
    });
  });

  describe('Given user is MENTOR', () => {
    it('when rendered, then only Mentor Dashboard is shown among restricted links', () => {
      renderLayout(['MENTOR']);
      expect(screen.getByRole('link', { name: /mentor dashboard/i })).toBeInTheDocument();
      expect(screen.queryByRole('link', { name: /^mentees$/i })).not.toBeInTheDocument();
      expect(screen.queryByRole('link', { name: /^users$/i })).not.toBeInTheDocument();
      expect(screen.queryByRole('link', { name: /^members$/i })).not.toBeInTheDocument();
    });
  });

  describe('Given user is MENTORSHIP_ADMIN', () => {
    it('when rendered, then Mentees, Mentors, and Members links are visible but not Mentor Dashboard or Users', () => {
      renderLayout(['MENTORSHIP_ADMIN']);
      expect(screen.getByRole('link', { name: /^mentees$/i })).toBeInTheDocument();
      expect(screen.getByRole('link', { name: /^mentors$/i })).toBeInTheDocument();
      expect(screen.getByRole('link', { name: /^members$/i })).toBeInTheDocument();
      expect(screen.queryByRole('link', { name: /mentor dashboard/i })).not.toBeInTheDocument();
      expect(screen.queryByRole('link', { name: /^users$/i })).not.toBeInTheDocument();
    });
  });

  describe('Given user is LEADER', () => {
    it('when rendered, then Mentors, Members, and Users links are visible but not Mentees or Mentor Dashboard', () => {
      renderLayout(['LEADER']);
      expect(screen.getByRole('link', { name: /^mentors$/i })).toBeInTheDocument();
      expect(screen.getByRole('link', { name: /^members$/i })).toBeInTheDocument();
      expect(screen.getByRole('link', { name: /^users$/i })).toBeInTheDocument();
      expect(screen.queryByRole('link', { name: /^mentees$/i })).not.toBeInTheDocument();
      expect(screen.queryByRole('link', { name: /mentor dashboard/i })).not.toBeInTheDocument();
    });
  });

  describe('Given user has no special roles', () => {
    it('when rendered, then only Dashboard and Logout are visible', () => {
      renderLayout([]);
      expect(screen.getByRole('link', { name: /^dashboard$/i })).toBeInTheDocument();
      expect(screen.getByRole('button', { name: /logout/i })).toBeInTheDocument();
      expect(screen.queryByRole('link', { name: /mentor dashboard/i })).not.toBeInTheDocument();
      expect(screen.queryByRole('link', { name: /^mentees$/i })).not.toBeInTheDocument();
      expect(screen.queryByRole('link', { name: /^mentors$/i })).not.toBeInTheDocument();
      expect(screen.queryByRole('link', { name: /^members$/i })).not.toBeInTheDocument();
      expect(screen.queryByRole('link', { name: /^users$/i })).not.toBeInTheDocument();
    });
  });
});
