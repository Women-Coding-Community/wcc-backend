import React from 'react';
import {render, screen} from '@testing-library/react';
import MenteeApplicationCard from '@/components/mentorship/MenteeApplicationCard';

const mockApp = {
  menteeId: 10,
  mentee: {
    fullName: 'Mentee Name',
    position: 'Developer',
  },
  status: 'PENDING',
  appliedAt: '2024-01-01T10:00:00Z',
  rejectionReason: 'Not a good fit',
};

describe('MenteeApplicationCard', () => {
  it('renders application info', () => {
    render(<MenteeApplicationCard application={mockApp as any}/>);

    expect(screen.getByText('Mentee Name')).toBeInTheDocument();
    expect(screen.getByText('Developer')).toBeInTheDocument();
    expect(screen.getByText('PENDING')).toBeInTheDocument();
  });

  it('shows rejection reason when requested', () => {
    render(<MenteeApplicationCard application={mockApp as any} showRejectionReason={true}/>);

    expect(screen.getByText(/Not a good fit/)).toBeInTheDocument();
  });

  it('hides rejection reason by default', () => {
    render(<MenteeApplicationCard application={mockApp as any}/>);

    expect(screen.queryByText(/Not a good fit/)).not.toBeInTheDocument();
  });
});
