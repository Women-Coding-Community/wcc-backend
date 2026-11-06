import '@testing-library/jest-dom';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import NewCycleDialog from '@/components/mentorship/NewCycleDialog';
import * as mentorshipService from '@/services/mentorshipService';

jest.mock('@/services/mentorshipService');

const mockCreateCycle = mentorshipService.createCycle as jest.Mock;

const createdCycle = {
  cycleId: 7,
  cycleYear: 2099,
  mentorshipType: 'Ad-Hoc',
  cycleMonth: 'MARCH',
  registrationStartDate: '2099-03-01',
  registrationEndDate: '2099-03-10',
  cycleStartDate: '2099-03-15',
  cycleEndDate: '2099-04-15',
  status: 'DRAFT',
};

function renderDialog() {
  const onCreated = jest.fn();
  const onClose = jest.fn();
  render(<NewCycleDialog open token="mock-token" onClose={onClose} onCreated={onCreated} />);
  return { onCreated, onClose };
}

function setDate(label: RegExp, value: string) {
  fireEvent.change(screen.getByLabelText(label), { target: { value } });
}

async function fillRequiredFields() {
  const user = userEvent.setup();
  const year = screen.getByLabelText(/^year/i);
  await user.clear(year);
  await user.type(year, '2099');
  await user.click(screen.getByRole('combobox', { name: /month/i }));
  await user.click(screen.getByRole('option', { name: 'March' }));
  await user.click(screen.getByRole('combobox', { name: /type/i }));
  await user.click(screen.getByRole('option', { name: 'Ad Hoc' }));
  setDate(/registration start/i, '2099-03-01');
  setDate(/registration end/i, '2099-03-10');
  setDate(/cycle start/i, '2099-03-15');
  return user;
}

describe('NewCycleDialog', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('renders all form fields', () => {
    renderDialog();

    expect(screen.getByLabelText(/^year/i)).toBeInTheDocument();
    expect(screen.getByRole('combobox', { name: /month/i })).toBeInTheDocument();
    expect(screen.getByRole('combobox', { name: /type/i })).toBeInTheDocument();
    expect(screen.getByLabelText(/registration start/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/registration end/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/cycle start/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/cycle end/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/max mentees per mentor/i)).toHaveValue(5);
    expect(screen.getByLabelText(/description/i)).toBeInTheDocument();
  });

  it('disables submit while required fields are empty', () => {
    renderDialog();

    expect(screen.getByRole('button', { name: 'Create cycle' })).toBeDisabled();
  });

  it('enables submit when required fields are filled', async () => {
    renderDialog();

    await fillRequiredFields();

    expect(screen.getByRole('button', { name: 'Create cycle' })).toBeEnabled();
  });

  it('shows an error when registration end is before registration start', async () => {
    renderDialog();

    await fillRequiredFields();
    setDate(/registration end/i, '2099-02-28');

    expect(screen.getByText('Must be on or after registration start')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Create cycle' })).toBeDisabled();
  });

  it('shows an error when cycle start is not after registration end', async () => {
    renderDialog();

    await fillRequiredFields();
    setDate(/cycle start/i, '2099-03-10');

    expect(screen.getByText('Must be after registration end')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Create cycle' })).toBeDisabled();
  });

  it('shows an error when cycle end is before cycle start', async () => {
    renderDialog();

    await fillRequiredFields();
    setDate(/cycle end/i, '2099-03-14');

    expect(screen.getByText('Must be on or after cycle start')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Create cycle' })).toBeDisabled();
  });

  it('shows an error when registration start is in the past', async () => {
    renderDialog();

    await fillRequiredFields();
    setDate(/registration start/i, '2000-01-01');

    expect(screen.getByText('Must be today or later')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Create cycle' })).toBeDisabled();
  });

  it('calls createCycle with the form values on submit', async () => {
    mockCreateCycle.mockResolvedValue(createdCycle);
    const { onCreated } = renderDialog();

    const user = await fillRequiredFields();
    setDate(/cycle end/i, '2099-04-15');
    await user.type(screen.getByLabelText(/description/i), 'Spring ad-hoc cycle');
    await user.click(screen.getByRole('button', { name: 'Create cycle' }));

    await waitFor(() => {
      expect(onCreated).toHaveBeenCalledWith(createdCycle);
    });
    expect(mockCreateCycle).toHaveBeenCalledWith(
      {
        cycleYear: 2099,
        cycleMonth: 'MARCH',
        mentorshipType: 'AD_HOC',
        registrationStartDate: '2099-03-01',
        registrationEndDate: '2099-03-10',
        cycleStartDate: '2099-03-15',
        cycleEndDate: '2099-04-15',
        maxMenteesPerMentor: 5,
        description: 'Spring ad-hoc cycle',
      },
      'mock-token'
    );
  });

  it('disables submit while the cycle is being created', async () => {
    mockCreateCycle.mockReturnValue(new Promise(() => {}));
    renderDialog();

    const user = await fillRequiredFields();
    const submit = screen.getByRole('button', { name: 'Create cycle' });
    await user.click(submit);

    expect(submit).toBeDisabled();
    expect(mockCreateCycle).toHaveBeenCalledTimes(1);
  });

  it('shows the API error message', async () => {
    mockCreateCycle.mockRejectedValue(new Error('Cycle already exists for 2099 Ad-Hoc MARCH'));
    const { onCreated } = renderDialog();

    const user = await fillRequiredFields();
    await user.click(screen.getByRole('button', { name: 'Create cycle' }));

    expect(
      await screen.findByText('Cycle already exists for 2099 Ad-Hoc MARCH')
    ).toBeInTheDocument();
    expect(onCreated).not.toHaveBeenCalled();
  });
});
