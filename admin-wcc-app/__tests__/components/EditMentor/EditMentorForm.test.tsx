import '@testing-library/jest-dom';
import { render, screen, waitFor } from '@testing-library/react';
import EditMentorForm from '@/components/EditMentor/EditMentorForm';
import * as mentorService from '@/services/mentorService';
import * as api from '@/lib/api';
import * as auth from '@/lib/auth';

// eslint-disable-next-line @typescript-eslint/no-require-imports
jest.mock('next/router', () => require('next-router-mock'));
jest.mock('@/services/mentorService');
jest.mock('@/lib/api');
jest.mock('@/lib/auth');

global.scrollTo = jest.fn();

const mockGetMentorById = mentorService.getMentorById as jest.Mock;
const mockApiFetch = api.apiFetch as jest.MockedFunction<typeof api.apiFetch>;
const mockGetStoredToken = auth.getStoredToken as jest.Mock;

const fakeMentor = {
  id: 7,
  fullName: 'Jane Smith',
  email: 'jane@example.com',
  position: 'Senior Engineer',
  slackDisplayName: 'janesmith',
  companyName: 'Acme',
  city: 'Berlin',
  country: { countryCode: 'DE', countryName: 'Germany' },
  profileStatus: 'ACTIVE',
  bio: 'Experienced engineer',
  spokenLanguages: ['English', 'German'],
  skills: {
    yearsExperience: 8,
    areas: [{ technicalArea: 'BACKEND', proficiencyLevel: 'EXPERT' }],
    languages: [{ language: 'JAVA', proficiencyLevel: 'EXPERT' }],
    mentorshipFocus: ['GROW_MID_TO_SENIOR'],
  },
  menteeSection: {
    idealMentee: 'Motivated learner',
    additional: 'Extra info',
    longTerm: null,
    adHoc: [{ month: 'JANUARY', hours: 2 }],
  },
  resources: {
    books: ['Clean Code', 'Refactoring'],
    links: [],
  },
  network: [],
};

describe('EditMentorForm', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockGetStoredToken.mockReturnValue('mock-token');
    mockApiFetch.mockResolvedValue(null);
  });

  it('Given a valid mentorId, when form loads, then mentor data is fetched and pre-fills the form', async () => {
    mockGetMentorById.mockResolvedValue(fakeMentor);

    render(<EditMentorForm mentorId="7" />);

    await waitFor(() => {
      expect(screen.getByDisplayValue('Jane Smith')).toBeInTheDocument();
      expect(screen.getByDisplayValue('jane@example.com')).toBeInTheDocument();
      expect(screen.getByDisplayValue('Senior Engineer')).toBeInTheDocument();
      expect(screen.getByDisplayValue('janesmith')).toBeInTheDocument();
    });
  });

  it('Given a valid mentorId, when form loads, then books array is joined as newline-separated text', async () => {
    mockGetMentorById.mockResolvedValue(fakeMentor);

    render(<EditMentorForm mentorId="7" />);

    await waitFor(() => {
      const booksField = screen.getByLabelText(/recommend books/i);
      expect(booksField).toHaveValue('Clean Code\nRefactoring');
    });
  });

  it('Given a valid mentorId with ACTIVE status, when form loads, then Approved status chip is shown', async () => {
    mockGetMentorById.mockResolvedValue(fakeMentor);

    render(<EditMentorForm mentorId="7" />);

    await waitFor(() => {
      expect(screen.getByText('Approved')).toBeInTheDocument();
    });
  });

  it('Given mentor fetch returns null, when form loads, then error message is shown', async () => {
    mockGetMentorById.mockResolvedValue(null);

    render(<EditMentorForm mentorId="99" />);

    await waitFor(() => {
      expect(screen.getByText(/mentor with id 99 not found/i)).toBeInTheDocument();
    });
  });

  it('Given mentor fetch fails, when form loads, then error message is shown', async () => {
    mockGetMentorById.mockRejectedValue(new Error('Network error'));

    render(<EditMentorForm mentorId="7" />);

    await waitFor(() => {
      expect(screen.getByText('Network error')).toBeInTheDocument();
    });
  });

  it('Given form is loaded, when Save Profile is clicked, then PUT request is made with mentor data', async () => {
    mockGetMentorById.mockResolvedValue(fakeMentor);
    mockApiFetch.mockResolvedValueOnce(null).mockResolvedValueOnce({});

    render(<EditMentorForm mentorId="7" />);

    await screen.findByDisplayValue('Jane Smith');

    const saveBtn = screen.getAllByRole('button', { name: /save profile/i })[0];
    saveBtn.click();

    await waitFor(() => {
      const calls = mockApiFetch.mock.calls.filter(
        (c) => c[0] === '/api/platform/v1/mentors/7' && (c[1] as any)?.method === 'PUT'
      );
      expect(calls.length).toBeGreaterThan(0);
    });
  });

  it('Given form is loaded, when Save Profile succeeds, then success message is shown', async () => {
    mockGetMentorById.mockResolvedValue(fakeMentor);
    mockApiFetch.mockResolvedValueOnce(null).mockResolvedValueOnce({});

    render(<EditMentorForm mentorId="7" />);

    await screen.findByDisplayValue('Jane Smith');

    const saveBtn = screen.getAllByRole('button', { name: /save profile/i })[0];
    saveBtn.click();

    await waitFor(() => {
      expect(screen.getByText('Profile updated successfully!')).toBeInTheDocument();
    });
  });

  it('Given form is loaded, when Save Profile fails, then error message is shown', async () => {
    mockGetMentorById.mockResolvedValue(fakeMentor);
    mockApiFetch.mockResolvedValueOnce(null).mockRejectedValueOnce(new Error('Save failed'));

    render(<EditMentorForm mentorId="7" />);

    await screen.findByDisplayValue('Jane Smith');

    const saveBtn = screen.getAllByRole('button', { name: /save profile/i })[0];
    saveBtn.click();

    await waitFor(() => {
      expect(screen.getByText('Save failed')).toBeInTheDocument();
    });
  });

  it('Given no token, when form loads, then getMentorById is not called', async () => {
    mockGetStoredToken.mockReturnValue(null);

    render(<EditMentorForm mentorId="7" />);

    await waitFor(() => {
      expect(mockGetMentorById).not.toHaveBeenCalled();
    });
  });
});
