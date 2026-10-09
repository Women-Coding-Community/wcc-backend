import { CycleStatus } from '@/types/mentorship';

export const CYCLE_STATUSES: { value: CycleStatus; label: string; description: string }[] = [
  {
    value: 'DRAFT',
    label: 'Draft',
    description: 'Cycle created but not yet open for registration',
  },
  { value: 'OPEN', label: 'Open', description: 'Registration is currently open' },
  { value: 'CLOSED', label: 'Closed', description: 'Registration has closed' },
  {
    value: 'IN_PROGRESS',
    label: 'In Progress',
    description: 'Cycle is active, mentorship ongoing',
  },
  { value: 'COMPLETED', label: 'Completed', description: 'Cycle has finished successfully' },
  { value: 'CANCELLED', label: 'Cancelled', description: 'Cycle was cancelled' },
];
