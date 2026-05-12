import React from 'react';
import {fireEvent, render, screen, waitFor} from '@testing-library/react';
import MatchCard from '@/components/mentorship/MatchCard';
import {createManualMatch} from '@/services/mentorshipService';

jest.mock('@/services/mentorshipService');

const mockMentor = {
  id: 1,
  fullName: 'Mentor Name',
};

const mockMentee = {
  id: 10,
  fullName: 'Mentee Name',
};

const mockMatch = {
  mentor: mockMentor,
  mentees: [
    {
      mentee: mockMentee,
      score: 80,
      applicationStatus: null,
    },
  ],
};

const onMenteeMatched = jest.fn();

describe('MatchCard', () => {
  it('renders mentor and mentee info', () => {
    render(
        <MatchCard
            match={mockMatch as any}
            cycleId={1}
            token="token"
            onMenteeMatched={onMenteeMatched}
        />
    );

    expect(screen.getByText(/Mentor #1/)).toBeInTheDocument();
    expect(screen.getByText(/Mentee Name/)).toBeInTheDocument();
    expect(screen.getByText(/Match with Mentee/)).toBeInTheDocument();
  });

  it('calls createManualMatch when button clicked', async () => {
    (createManualMatch as jest.Mock).mockResolvedValue({});
    render(
        <MatchCard
            match={mockMatch as any}
            cycleId={1}
            token="token"
            onMenteeMatched={onMenteeMatched}
        />
    );

    fireEvent.click(screen.getByText(/Match with Mentee/));

    await waitFor(() => {
      expect(createManualMatch).toHaveBeenCalledWith(10, 1, 1, 'token');
      expect(onMenteeMatched).toHaveBeenCalledWith(1, 10);
    });
  });

  it('disables button and shows status if applicationStatus is present', () => {
    const matchWithStatus = {
      ...mockMatch,
      mentees: [
        {
          ...mockMatch.mentees[0],
          applicationStatus: 'PENDING',
        },
      ],
    };

    render(
        <MatchCard
            match={matchWithStatus as any}
            cycleId={1}
            token="token"
            onMenteeMatched={onMenteeMatched}
        />
    );

    expect(screen.queryByText(/Match with Mentee/)).not.toBeInTheDocument();
    expect(screen.getByText('PENDING')).toBeInTheDocument();
  });
});
