import { ChangeEvent, useState } from 'react';

import {
  Alert,
  Box,
  Button,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  MenuItem,
  TextField,
} from '@mui/material';
import { getErrorMessage } from '@/lib/api';
import { MENTORSHIP_TYPES } from '@/lib/mentorshipTypes';
import { createCycle } from '@/services/mentorshipService';
import { MentorshipCycle, NewCycleRequest } from '@/types/mentorship';

const CYCLE_MONTHS = [
  'FEBRUARY',
  'MARCH',
  'APRIL',
  'MAY',
  'JUNE',
  'JULY',
  'AUGUST',
  'SEPTEMBER',
  'OCTOBER',
  'NOVEMBER',
];

type CycleForm = {
  cycleYear: string;
  cycleMonth: string;
  mentorshipType: string;
  registrationStartDate: string;
  registrationEndDate: string;
  cycleStartDate: string;
  cycleEndDate: string;
  maxMenteesPerMentor: string;
  description: string;
};

type FormErrors = Partial<Record<keyof CycleForm, string>>;

const REQUIRED_FIELDS: (keyof CycleForm)[] = [
  'cycleYear',
  'cycleMonth',
  'mentorshipType',
  'registrationStartDate',
  'registrationEndDate',
  'cycleStartDate',
  'maxMenteesPerMentor',
];

function emptyForm(): CycleForm {
  return {
    cycleYear: String(new Date().getFullYear()),
    cycleMonth: '',
    mentorshipType: '',
    registrationStartDate: '',
    registrationEndDate: '',
    cycleStartDate: '',
    cycleEndDate: '',
    maxMenteesPerMentor: '5',
    description: '',
  };
}

function today(): string {
  return new Date().toLocaleDateString('en-CA');
}

function monthLabel(month: string): string {
  return month.charAt(0) + month.slice(1).toLowerCase();
}

function validateForm(form: CycleForm): FormErrors {
  const errors: FormErrors = {};
  if (form.cycleYear && Number(form.cycleYear) < new Date().getFullYear()) {
    errors.cycleYear = 'Must be this year or later';
  }
  if (form.maxMenteesPerMentor && Number(form.maxMenteesPerMentor) < 1) {
    errors.maxMenteesPerMentor = 'Must be at least 1';
  }
  if (form.registrationStartDate && form.registrationStartDate < today()) {
    errors.registrationStartDate = 'Must be today or later';
  }
  if (
    form.registrationStartDate &&
    form.registrationEndDate &&
    form.registrationEndDate < form.registrationStartDate
  ) {
    errors.registrationEndDate = 'Must be on or after registration start';
  }
  if (
    form.registrationEndDate &&
    form.cycleStartDate &&
    form.cycleStartDate <= form.registrationEndDate
  ) {
    errors.cycleStartDate = 'Must be after registration end';
  }
  if (form.cycleStartDate && form.cycleEndDate && form.cycleEndDate < form.cycleStartDate) {
    errors.cycleEndDate = 'Must be on or after cycle start';
  }
  return errors;
}

function toRequest(form: CycleForm): NewCycleRequest {
  return {
    cycleYear: Number(form.cycleYear),
    cycleMonth: form.cycleMonth,
    mentorshipType: form.mentorshipType,
    registrationStartDate: form.registrationStartDate,
    registrationEndDate: form.registrationEndDate,
    cycleStartDate: form.cycleStartDate,
    cycleEndDate: form.cycleEndDate || undefined,
    maxMenteesPerMentor: Number(form.maxMenteesPerMentor),
    description: form.description.trim() || undefined,
  };
}

interface NewCycleDialogProps {
  open: boolean;
  token: string;
  onClose: () => void;
  onCreated: (cycle: MentorshipCycle) => void;
}

export default function NewCycleDialog({ open, token, onClose, onCreated }: NewCycleDialogProps) {
  const [form, setForm] = useState<CycleForm>(emptyForm);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const errors = validateForm(form);
  const hasMissingFields = REQUIRED_FIELDS.some((field) => !form[field].trim());
  const canSubmit = !hasMissingFields && Object.keys(errors).length === 0 && !submitting;

  function handleChange(field: keyof CycleForm) {
    return (event: ChangeEvent<HTMLInputElement>) =>
      setForm((current) => ({ ...current, [field]: event.target.value }));
  }

  function handleClose() {
    if (submitting) return;
    setForm(emptyForm());
    setError(null);
    onClose();
  }

  async function handleSubmit() {
    setError(null);
    setSubmitting(true);
    try {
      const cycle = await createCycle(toRequest(form), token);
      setForm(emptyForm());
      onCreated(cycle);
    } catch (error: unknown) {
      setError(getErrorMessage(error, 'Failed to create cycle'));
    } finally {
      setSubmitting(false);
    }
  }

  function dateField(field: keyof CycleForm, label: string, required = true) {
    return (
      <TextField
        type="date"
        label={label}
        required={required}
        value={form[field]}
        onChange={handleChange(field)}
        error={Boolean(errors[field])}
        helperText={errors[field]}
        slotProps={{ inputLabel: { shrink: true }, htmlInput: { min: today() } }}
      />
    );
  }

  return (
    <Dialog open={open} onClose={handleClose} maxWidth="sm" fullWidth>
      <DialogTitle>New cycle</DialogTitle>
      <DialogContent>
        {error && (
          <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError(null)}>
            {error}
          </Alert>
        )}
        <Box
          display="grid"
          gridTemplateColumns={{ xs: '1fr', sm: '1fr 1fr' }}
          gap={2}
          sx={{ pt: 1 }}
        >
          <TextField
            type="number"
            label="Year"
            required
            value={form.cycleYear}
            onChange={handleChange('cycleYear')}
            error={Boolean(errors.cycleYear)}
            helperText={errors.cycleYear}
          />
          <TextField
            select
            label="Month"
            required
            value={form.cycleMonth}
            onChange={handleChange('cycleMonth')}
          >
            {CYCLE_MONTHS.map((month) => (
              <MenuItem key={month} value={month}>
                {monthLabel(month)}
              </MenuItem>
            ))}
          </TextField>
          <TextField
            select
            label="Type"
            required
            value={form.mentorshipType}
            onChange={handleChange('mentorshipType')}
          >
            {MENTORSHIP_TYPES.map((type) => (
              <MenuItem key={type.value} value={type.value}>
                {type.label}
              </MenuItem>
            ))}
          </TextField>
          <TextField
            type="number"
            label="Max mentees per mentor"
            required
            value={form.maxMenteesPerMentor}
            onChange={handleChange('maxMenteesPerMentor')}
            error={Boolean(errors.maxMenteesPerMentor)}
            helperText={errors.maxMenteesPerMentor}
            slotProps={{ htmlInput: { min: 1 } }}
          />
          {dateField('registrationStartDate', 'Registration start')}
          {dateField('registrationEndDate', 'Registration end')}
          {dateField('cycleStartDate', 'Cycle start')}
          {dateField('cycleEndDate', 'Cycle end', false)}
          <TextField
            label="Description"
            multiline
            minRows={3}
            value={form.description}
            onChange={handleChange('description')}
            sx={{ gridColumn: '1 / -1' }}
          />
        </Box>
      </DialogContent>
      <DialogActions>
        <Button onClick={handleClose} disabled={submitting}>
          Cancel
        </Button>
        <Button onClick={handleSubmit} variant="contained" disabled={!canSubmit}>
          {submitting ? <CircularProgress size={18} /> : 'Create cycle'}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
