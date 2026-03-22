import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import BioSection from '@/components/mentors/BioSection';

const SHORT_BIO = 'Short bio.';
const LONG_BIO = 'a'.repeat(300);
const TRUNCATED_PREVIEW = LONG_BIO.substring(0, 260) + '…';

describe('BioSection', () => {
  it('Given bio is shorter than 260 chars, when rendered, then full text is shown with no toggle button', () => {
    render(<BioSection bio={SHORT_BIO} />);

    expect(screen.getByText(SHORT_BIO)).toBeInTheDocument();
    expect(screen.queryByRole('button')).not.toBeInTheDocument();
  });

  it('Given bio is longer than 260 chars, when rendered, then text is truncated and See more button is shown', () => {
    render(<BioSection bio={LONG_BIO} />);

    expect(screen.getByText(TRUNCATED_PREVIEW)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /see more/i })).toBeInTheDocument();
  });

  it('Given bio is longer than 260 chars, when See more is clicked, then full bio is shown and button changes to See less', async () => {
    const user = userEvent.setup();
    render(<BioSection bio={LONG_BIO} />);

    await user.click(screen.getByRole('button', { name: /see more/i }));

    expect(screen.getByText(LONG_BIO)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /see less/i })).toBeInTheDocument();
  });

  it('Given bio is expanded, when See less is clicked, then bio is truncated again', async () => {
    const user = userEvent.setup();
    render(<BioSection bio={LONG_BIO} />);

    await user.click(screen.getByRole('button', { name: /see more/i }));
    await user.click(screen.getByRole('button', { name: /see less/i }));

    expect(screen.getByText(TRUNCATED_PREVIEW)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /see more/i })).toBeInTheDocument();
  });
});
