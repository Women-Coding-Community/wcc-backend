import '@testing-library/jest-dom';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import CreateMentorForm from '@/components/CreateMentor/CreateMentorForm';
import * as api from '@/lib/api';
import mockRouter from 'next-router-mock';

jest.mock('next/router', () => jest.requireActual('next-router-mock'));
jest.mock('@/lib/api');
jest.mock('@/lib/auth', () => ({
  getStoredToken: jest.fn(() => 'mock-token'),
}));

global.scrollTo = jest.fn();

const mockApiFetch = api.apiFetch as jest.MockedFunction<typeof api.apiFetch>;

const fillRequiredFields = async (user: ReturnType<typeof userEvent.setup>) => {
  await user.type(screen.getByLabelText(/full name/i), 'Jane Doe');
  await user.type(screen.getByLabelText(/email/i), 'jane@example.com');
  await user.type(screen.getByLabelText(/position/i), 'Developer');
  await user.type(screen.getByLabelText(/slack display name/i), 'janedoe');
  await user.type(screen.getByLabelText(/bio/i), 'Experienced developer');
  await user.type(screen.getByLabelText(/years of experience/i), '5');
  await user.type(screen.getByLabelText(/ideal mentee/i), 'Eager learners');

  const countryInput = screen.getByLabelText(/country/i);
  await user.click(countryInput);
  await user.type(countryInput, 'United');
  const countryOption = await screen.findByRole('option', { name: /united states \(us\)/i });
  await user.click(countryOption);

  const profileStatusLabel = screen.getByText('Profile Status');
  const profileStatusFormControl = profileStatusLabel.closest('.MuiFormControl-root');
  const profileStatusSelect = profileStatusFormControl?.querySelector(
    '[role="combobox"]'
  ) as HTMLElement;
  fireEvent.mouseDown(profileStatusSelect);
  const activeOption = await screen.findByRole('option', { name: /active/i });
  await user.click(activeOption);

  const technicalAreasInput = screen.getByLabelText(/technical areas/i);
  await user.click(technicalAreasInput);
  const backendOption = await screen.findByRole('option', { name: /backend/i });
  await user.click(backendOption);
  await user.click(screen.getByText('Skills & Experience'));

  const progLangInput = screen.getByLabelText(/programming languages/i);
  await user.click(progLangInput);
  const javaOption = await screen.findByRole('option', { name: /^java$/i });
  await user.click(javaOption);
  await user.click(screen.getByText('Skills & Experience'));

  const focusInput = screen.getByLabelText(/mentorship focus/i);
  await user.click(focusInput);
  const careerOption = await screen.findByRole('option', { name: /career/i });
  await user.click(careerOption);
  await user.click(screen.getByText('Mentorship Preferences'));

  const typeInput = screen.getByLabelText(/mentorship type/i);
  await user.click(typeInput);
  const adHocOption = await screen.findByRole('option', { name: /ad hoc/i });
  await user.click(adHocOption);
  await user.click(screen.getByText('Mentorship Preferences'));
};

describe('CreateMentorForm', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it.each([
    { field: 'Full Name', errorMessage: 'Full name is required' },
    { field: 'Email', errorMessage: 'Email is required' },
    { field: 'Position', errorMessage: 'Position is required' },
    { field: 'Slack Display Name', errorMessage: 'Slack display name is required' },
    { field: 'Bio', errorMessage: 'Bio is required' },
    { field: 'Years of Experience', errorMessage: 'Years of experience is required' },
    { field: 'Ideal Mentee', errorMessage: 'Ideal mentee description is required' },
  ])('does not submit form when $field is empty', async ({ errorMessage }) => {
    render(<CreateMentorForm />);

    const submitButton = screen.getByRole('button', { name: /create mentor/i });
    fireEvent.click(submitButton);

    await screen.findByText(errorMessage);
    expect(mockApiFetch).not.toHaveBeenCalled();
  });

  it('shows email validation error for invalid email format', async () => {
    const user = userEvent.setup();
    render(<CreateMentorForm />);

    const emailInput = screen.getByLabelText(/email/i);
    await user.type(emailInput, 'invalid-email');
    await user.tab();

    await screen.findByText('Invalid email format');
    expect(mockApiFetch).not.toHaveBeenCalled();
  });

  it('shows success message when API call succeeds', async () => {
    mockApiFetch.mockResolvedValueOnce({});
    const user = userEvent.setup();
    render(<CreateMentorForm />);

    await fillRequiredFields(user);

    const submitButton = screen.getByRole('button', { name: /create mentor/i });
    await user.click(submitButton);

    await screen.findByText('Mentor created successfully!');

    expect(mockApiFetch).toHaveBeenCalledWith(
      '/api/platform/v1/mentors',
      expect.objectContaining({
        method: 'POST',
        token: 'mock-token',
      })
    );
  });

  it('shows error message when API call fails', async () => {
    mockApiFetch.mockRejectedValueOnce(new Error('Server error'));
    const user = userEvent.setup();
    render(<CreateMentorForm />);

    await fillRequiredFields(user);

    const submitButton = screen.getByRole('button', { name: /create mentor/i });
    await user.click(submitButton);

    await screen.findByText('Server error');
  });

  it('sends correct payload to API when form is submitted', async () => {
    mockApiFetch.mockResolvedValueOnce({});
    const user = userEvent.setup();
    render(<CreateMentorForm />);

    await fillRequiredFields(user);

    const submitButton = screen.getByRole('button', { name: /create mentor/i });
    await user.click(submitButton);

    await waitFor(() => {
      expect(mockApiFetch).toHaveBeenCalled();
    });

    const callArgs = mockApiFetch.mock.calls[0];
    const payload = callArgs[1]?.body;

    expect(payload).toMatchObject({
      fullName: 'Jane Doe',
      email: 'jane@example.com',
      position: 'Developer',
      slackDisplayName: 'janedoe',
      country: {
        countryCode: 'US',
        countryName: 'United States',
      },
      memberTypes: ['MENTOR'],
      profileStatus: 'ACTIVE',
      bio: 'Experienced developer',
      skills: {
        yearsExperience: 5,
        areas: expect.arrayContaining(['BACKEND']),
        languages: expect.arrayContaining(['JAVA']),
        mentorshipFocus: expect.any(Array),
      },
      menteeSection: {
        mentorshipType: expect.arrayContaining(['AD_HOC']),
        availability: [],
        idealMentee: 'Eager learners',
        additional: '',
      },
    });
  });

  it('navigates to mentors list when cancel button is clicked', async () => {
    const user = userEvent.setup();
    mockRouter.setCurrentUrl('/admin/mentors/create');
    render(<CreateMentorForm />);

    const cancelButton = screen.getByRole('button', { name: /cancel/i });
    await user.click(cancelButton);

    expect(mockRouter.pathname).toBe('/admin/mentors');
  });
});
