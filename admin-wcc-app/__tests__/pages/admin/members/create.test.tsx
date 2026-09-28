import '@testing-library/jest-dom';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import CreateMemberPage from '@/pages/admin/members/create';
import * as api from '@/lib/api';
import * as AuthProvider from '@/components/AuthProvider';
import mockRouter from 'next-router-mock';

jest.mock('next/router', () => jest.requireActual('next-router-mock'));
jest.mock('@/lib/api');
jest.mock('@/components/AuthProvider');
jest.mock('@/lib/auth', () => ({
  getStoredToken: jest.fn(() => 'mock-token'),
}));

global.scrollTo = jest.fn();

const mockApiFetch = api.apiFetch as jest.MockedFunction<typeof api.apiFetch>;
const mockUseAuth = AuthProvider.useAuth as jest.Mock;

describe('CreateMemberPage', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockRouter.setCurrentUrl('/admin/members/create');

    mockUseAuth.mockReturnValue({
      token: 'mock-token',
      roles: ['ADMIN'],
      member: null,
      logout: jest.fn(),
    });
  });

  it('trims full name before submitting member data', async () => {
    mockApiFetch.mockResolvedValueOnce({});

    const user = userEvent.setup();

    render(<CreateMemberPage />);

    await user.type(screen.getByLabelText(/full name/i), '  Jane Doe  ');
    await user.type(screen.getByLabelText(/email/i), 'jane@example.com');
    await user.type(screen.getByLabelText(/position/i), 'Developer');
    await user.type(screen.getByLabelText(/slack display name/i), 'janedoe');

    const countryInput = screen.getByLabelText(/country/i);
    await user.click(countryInput);
    await user.type(countryInput, 'United');

    const countryOption = await screen.findByRole('option', {
      name: /united states \(us\)/i,
    });
    await user.click(countryOption);

    const memberTypesInput = screen.getByLabelText(/member types/i);
    await user.click(memberTypesInput);

    const memberOption = await screen.findByRole('option', {
      name: /^member$/i,
    });
    await user.click(memberOption);

    await user.click(screen.getByRole('button', { name: /create member/i }));

    await waitFor(() => {
      expect(mockApiFetch).toHaveBeenCalled();
    });

    const payload = mockApiFetch.mock.calls[0][1]?.body;

    expect(payload).toMatchObject({
      fullName: 'Jane Doe',
    });
  });
});
